package com.sil.club.utils;

import java.util.Collections;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * MyBatis-Plus 代码一键生成器
 */
public class CodeGenerator {

    public static void main(String[] args) {
        
        // 1. 获取当前项目的根目录路径
        String projectPath = System.getProperty("user.dir");

        // 2. 核心配置：数据库连接信息 (🚨请务必把密码改成你自己的 MySQL 密码！)
        String url = "jdbc:mysql://localhost:3306/club_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        String username = "root";
        String password = "071370"; // <--- 这里改成你的密码

        // 3. 开始执行生成
        FastAutoGenerator.create(url, username, password)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("sil") // 设置代码的作者名字
                           .outputDir(projectPath + "/src/main/java"); // 指定生成的 Java 代码输出目录
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("com.sil") // 设置父包名
                           .moduleName("club") // 设置模块名
                           // 指定 mapper.xml 文件的输出目录到 resources 下
                           .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/resources/mapper"));
                })
                // 策略配置 (核心)
                .strategyConfig(builder -> {
                    builder.addInclude("user", "club", "activity", "registration", "point_record") // 需要生成代码的 5 张表
                           .entityBuilder().enableLombok() // 实体类自动加上 @Data 等 Lombok 注解
                           .controllerBuilder().enableRestStyle() // Controller 自动加上 @RestController
                           .mapperBuilder().enableMapperAnnotation(); // Mapper 接口自动加上 @Mapper 注解
                })
                // 使用 Freemarker 模板引擎
                .templateEngine(new FreemarkerTemplateEngine())
                .execute(); // 🚀 启动！
    }
}