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

        String projectPath = System.getProperty("user.dir");

        String url = "jdbc:mysql://localhost:3306/club_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        String username = "root";
        String password = "071370";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("sil")
                            .outputDir(projectPath + "/src/main/java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.sil")
                            .moduleName("club")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/resources/mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user", "club", "activity", "registration", "point_record")
                            .entityBuilder().enableLombok()
                            .controllerBuilder().enableRestStyle()
                            .mapperBuilder().enableMapperAnnotation();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
