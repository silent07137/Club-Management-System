环境要求：JDK 17+、Node.js 16+。

1. 默认启动：双击或执行 `start.bat`，后端默认使用 H2，前端会一起启动。
2. IDE 启动：直接运行 `silent_backend/src/main/java/com/sil/club/ClubApplication.java` 也可以，默认同样走 H2。
3. MySQL 启动：如果你要切回 MySQL，先确保本机 `MySQL80` 服务已启动，然后运行 `start-mysql.bat`，或者在 IDE 里把 Spring profile 设为 `mysql`。
4. 数据库脚本：MySQL 方案对应 `sql/club_system.sql`。
5. 前端单独启动：进入 `silent_frontend/club-system-frontend` 目录，先执行 `npm install`，再执行 `npm run dev`，然后访问控制台输出的 Local URL。

默认管理员用户：`admin`
默认密码：`123456`
