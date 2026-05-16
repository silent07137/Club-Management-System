using System.Diagnostics;
using System.Text;

namespace ClubSystemLauncher;

internal static class Program
{
    [STAThread]
    private static void Main()
    {
        if (argsHasStopRequest(Environment.GetCommandLineArgs()))
        {
            MainForm.StopPorts();
            return;
        }

        ApplicationConfiguration.Initialize();
        Application.Run(new MainForm());
    }

    private static bool argsHasStopRequest(string[] args)
    {
        return args.Any(arg => arg.Equals("--stop", StringComparison.OrdinalIgnoreCase)
            || arg.Equals("-stop", StringComparison.OrdinalIgnoreCase));
    }
}

internal sealed class MainForm : Form
{
    private readonly Label _backendStatus;
    private readonly Label _frontendStatus;
    private readonly ComboBox _profileBox;
    private readonly TextBox _logBox;
    private readonly System.Windows.Forms.Timer _timer;

    private readonly string _rootDir;
    private readonly string _backendDir;
    private readonly string _frontendDir;

    public MainForm()
    {
        _rootDir = LocateRoot();
        _backendDir = Path.Combine(_rootDir, "silent_backend");
        _frontendDir = Path.Combine(_rootDir, "silent_frontend", "club-system-frontend");

        Text = "社团系统控制台";
        StartPosition = FormStartPosition.CenterScreen;
        MinimumSize = new Size(560, 360);
        Size = new Size(620, 420);
        BackColor = Color.FromArgb(245, 247, 250);
        AutoScaleMode = AutoScaleMode.Font;
        Font = new Font("Microsoft YaHei UI", 9F, FontStyle.Regular, GraphicsUnit.Point);

        var root = new TableLayoutPanel
        {
            Dock = DockStyle.Fill,
            ColumnCount = 1,
            RowCount = 4,
            Padding = new Padding(16),
        };
        root.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        root.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        root.RowStyles.Add(new RowStyle(SizeType.Percent, 100F));
        root.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        Controls.Add(root);

        var title = new Label
        {
            Text = "社团系统控制台",
            AutoSize = true,
            Font = new Font("Microsoft YaHei UI", 16F, FontStyle.Bold),
            Dock = DockStyle.Top,
            Margin = new Padding(0, 0, 0, 12)
        };
        root.Controls.Add(title, 0, 0);

        var statusPanel = new TableLayoutPanel
        {
            Dock = DockStyle.Top,
            ColumnCount = 2,
            RowCount = 2,
            AutoSize = true,
            Margin = new Padding(0, 0, 0, 12)
        };
        statusPanel.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50F));
        statusPanel.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50F));
        statusPanel.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        statusPanel.RowStyles.Add(new RowStyle(SizeType.AutoSize));

        _backendStatus = CreateStatusLabel();
        _frontendStatus = CreateStatusLabel();

        statusPanel.Controls.Add(_backendStatus, 0, 0);
        statusPanel.Controls.Add(_frontendStatus, 1, 0);
        statusPanel.SetColumnSpan(_backendStatus, 1);
        statusPanel.SetColumnSpan(_frontendStatus, 1);
        root.Controls.Add(statusPanel, 0, 1);

        var controlCard = new GroupBox
        {
            Text = "操作",
            Dock = DockStyle.Top,
            AutoSize = true,
            Padding = new Padding(12),
            Margin = new Padding(0, 0, 0, 12)
        };
        root.Controls.Add(controlCard, 0, 2);

        var controlGrid = new TableLayoutPanel
        {
            Dock = DockStyle.Top,
            ColumnCount = 4,
            RowCount = 2,
            AutoSize = true
        };
        controlGrid.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));
        controlGrid.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 100F));
        controlGrid.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));
        controlGrid.ColumnStyles.Add(new ColumnStyle(SizeType.AutoSize));
        controlGrid.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        controlGrid.RowStyles.Add(new RowStyle(SizeType.AutoSize));
        controlCard.Controls.Add(controlGrid);

        var profileLabel = new Label
        {
            Text = "启动模式",
            AutoSize = true,
            Anchor = AnchorStyles.Left,
            Margin = new Padding(0, 8, 8, 0)
        };
        controlGrid.Controls.Add(profileLabel, 0, 0);

        _profileBox = new ComboBox
        {
            DropDownStyle = ComboBoxStyle.DropDownList,
            Width = 160,
            Anchor = AnchorStyles.Left,
            Margin = new Padding(0, 4, 12, 8)
        };
        _profileBox.Items.AddRange(["H2", "MySQL", "默认"]);
        _profileBox.SelectedIndex = 0;
        controlGrid.Controls.Add(_profileBox, 1, 0);

        var startButton = new Button
        {
            Text = "启动",
            AutoSize = true,
            Anchor = AnchorStyles.Left,
            Margin = new Padding(0, 4, 8, 8)
        };
        startButton.Click += (_, _) => StartServices();
        controlGrid.Controls.Add(startButton, 2, 0);

        var stopButton = new Button
        {
            Text = "停止",
            AutoSize = true,
            Anchor = AnchorStyles.Left,
            Margin = new Padding(0, 4, 0, 8)
        };
        stopButton.Click += (_, _) => StopServices();
        controlGrid.Controls.Add(stopButton, 3, 0);

        var refreshButton = new Button
        {
            Text = "刷新状态",
            AutoSize = true,
            Anchor = AnchorStyles.Left,
            Margin = new Padding(0, 0, 8, 0)
        };
        refreshButton.Click += (_, _) => RefreshStatus();
        controlGrid.Controls.Add(refreshButton, 2, 1);

        var openButton = new Button
        {
            Text = "打开页面",
            AutoSize = true,
            Anchor = AnchorStyles.Left,
            Margin = new Padding(0, 0, 0, 0)
        };
        openButton.Click += (_, _) => OpenApp();
        controlGrid.Controls.Add(openButton, 3, 1);

        var logGroup = new GroupBox
        {
            Text = "运行日志",
            Dock = DockStyle.Fill,
            Padding = new Padding(12)
        };
        root.Controls.Add(logGroup, 0, 3);

        _logBox = new TextBox
        {
            Multiline = true,
            ReadOnly = true,
            ScrollBars = ScrollBars.Vertical,
            Dock = DockStyle.Fill,
            BackColor = Color.White
        };
        logGroup.Controls.Add(_logBox);

        _timer = new System.Windows.Forms.Timer { Interval = 2000 };
        _timer.Tick += (_, _) => RefreshStatus();
        _timer.Start();

        Shown += (_, _) =>
        {
            RefreshStatus();
            Log("启动器已就绪");
        };

        FormClosing += (_, _) => StopPorts();
    }

    private static Label CreateStatusLabel()
    {
        return new Label
        {
            AutoSize = false,
            Height = 38,
            Dock = DockStyle.Fill,
            TextAlign = ContentAlignment.MiddleLeft,
            Padding = new Padding(10, 8, 10, 8),
            BorderStyle = BorderStyle.FixedSingle,
            Margin = new Padding(0, 0, 8, 8),
            BackColor = Color.White
        };
    }

    private static string LocateRoot()
    {
        var current = AppContext.BaseDirectory;
        var dir = new DirectoryInfo(current);
        while (dir is not null)
        {
            if (Directory.Exists(Path.Combine(dir.FullName, "silent_backend")) &&
                Directory.Exists(Path.Combine(dir.FullName, "silent_frontend")))
            {
                return dir.FullName;
            }

            dir = dir.Parent;
        }

        return AppContext.BaseDirectory;
    }

    private void Log(string message)
    {
        var stamp = DateTime.Now.ToString("HH:mm:ss");
        _logBox.AppendText($"[{stamp}] {message}{Environment.NewLine}");
    }

    private void RefreshStatus()
    {
        var backendRunning = IsPortListening(8080);
        var frontendRunning = IsPortListening(5173);

        ApplyStatus(_backendStatus, "后端", backendRunning);
        ApplyStatus(_frontendStatus, "前端", frontendRunning);
    }

    private static void ApplyStatus(Control label, string name, bool running)
    {
        label.Text = $"{name}：{(running ? "运行中" : "已停止")}";
        label.ForeColor = running ? Color.ForestGreen : Color.IndianRed;
        label.BackColor = running ? Color.FromArgb(235, 250, 240) : Color.FromArgb(252, 238, 238);
    }

    private void StartServices()
    {
        var profile = _profileBox.SelectedItem?.ToString() ?? "H2";
        var backendArgs = profile switch
        {
            "MySQL" => "-Dspring-boot.run.profiles=mysql spring-boot:run",
            "默认" => "spring-boot:run",
            _ => "-Dspring-boot.run.profiles=h2 spring-boot:run"
        };

        if (!IsPortListening(8080))
        {
            StartHiddenCommand("cmd.exe", $"/c cd /d \"{_backendDir}\" && mvnw.cmd {backendArgs}");
        }

        if (!IsPortListening(5173))
        {
            StartHiddenCommand("cmd.exe", $"/c cd /d \"{_frontendDir}\" && npm run dev");
        }

        Log($"已发起启动：{profile}");
        RefreshStatus();
    }

    private void StopServices()
    {
        var stopped = StopPorts();
        Log(stopped ? "已发起停止" : "未发现运行中的服务");
        RefreshStatus();
    }

    private void OpenApp()
    {
        StartHiddenCommand("cmd.exe", "/c start \"\" http://localhost:5173");
    }

    private static void StartHiddenCommand(string fileName, string arguments)
    {
        var psi = new ProcessStartInfo(fileName, arguments)
        {
            WorkingDirectory = Environment.CurrentDirectory,
            UseShellExecute = false,
            CreateNoWindow = true,
            WindowStyle = ProcessWindowStyle.Hidden
        };
        Process.Start(psi);
    }

    private static bool IsPortListening(int port)
    {
        var psi = new ProcessStartInfo("cmd.exe", $"/c netstat -ano | findstr :{port}")
        {
            UseShellExecute = false,
            CreateNoWindow = true,
            RedirectStandardOutput = true,
            RedirectStandardError = true
        };

        using var process = Process.Start(psi);
        if (process is null)
        {
            return false;
        }

        var output = process.StandardOutput.ReadToEnd();
        process.WaitForExit(2000);

        foreach (var line in output.Split(['\r', '\n'], StringSplitOptions.RemoveEmptyEntries))
        {
            if (line.Contains("LISTENING", StringComparison.OrdinalIgnoreCase))
            {
                return true;
            }
        }

        return false;
    }

    private static IEnumerable<int> GetListeningPids(int port)
    {
        var psi = new ProcessStartInfo("cmd.exe", $"/c netstat -ano | findstr :{port}")
        {
            UseShellExecute = false,
            CreateNoWindow = true,
            RedirectStandardOutput = true,
            RedirectStandardError = true
        };

        using var process = Process.Start(psi);
        if (process is null)
        {
            yield break;
        }

        var output = process.StandardOutput.ReadToEnd();
        process.WaitForExit(2000);

        foreach (var line in output.Split(['\r', '\n'], StringSplitOptions.RemoveEmptyEntries))
        {
            if (line.Contains("LISTENING", StringComparison.OrdinalIgnoreCase))
            {
                var parts = line.Split(' ', StringSplitOptions.RemoveEmptyEntries);
                if (parts.Length > 0 && int.TryParse(parts[^1], out var pid))
                {
                    yield return pid;
                }
            }
        }
    }

    internal static bool StopPorts()
    {
        var any = false;
        foreach (var port in new[] { 8080, 5173 })
        {
            foreach (var pid in GetListeningPids(port).Distinct())
            {
                try
                {
                    Process.GetProcessById(pid).Kill(true);
                    any = true;
                }
                catch
                {
                }
            }
        }

        Thread.Sleep(700);

        foreach (var port in new[] { 8080, 5173 })
        {
            foreach (var pid in GetListeningPids(port).Distinct())
            {
                try
                {
                    Process.GetProcessById(pid).Kill(true);
                    any = true;
                }
                catch
                {
                }
            }
        }

        return any;
    }
}
