package cn.sbx0.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class CodeGenerator {
    public static final String author = "wangh";
    public static final String packageConfig = "cn.sbx0.microservices";
    public static final String moduleName = "microservices-uno";
    public static final String modulePackageConfig = "uno";
    public static final String table = "game_result";
    public static String url;
    public static String username;
    public static String password;

    /**
     * CODE_GENERATOR_URL=jdbc:mysql://wsl2.sbx0.cn:3306/assembler;CODE_GENERATOR_USERNAME=root;CODE_GENERATOR_PASSWORD=test;
     */
    public static void main(String[] args) {
        Map<String, String> environment = System.getenv();
        url = environment.get("CODE_GENERATOR_URL");
        username = environment.get("CODE_GENERATOR_USERNAME");
        password = environment.get("CODE_GENERATOR_PASSWORD");
        FastAutoGenerator
                .create(url, username, password)
                .globalConfig(builder -> builder.author(author)
                        .fileOverride()
                        .outputDir(System.getProperty("user.dir") + File.separator + moduleName + File.separator + "src" + File.separator + "main" + File.separator + "java"))
                .packageConfig(builder -> builder.parent(packageConfig)
                        .moduleName(modulePackageConfig)
                        .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir") + File.separator + moduleName + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "mapper")))
                .strategyConfig(builder -> builder.addInclude(table).build()
                        .entityBuilder()
                        .enableLombok()
                        .formatFileName("%sEntity")
                        .build()
                        .controllerBuilder()
                        .enableRestStyle()
                        .build())
                .templateConfig(builder -> builder.entity("/templates/entity.java")
                        .service("/templates/service.java")
                        .serviceImpl("/templates/serviceImpl.java")
                        .mapper("/templates/mapper.java")
                        .mapperXml("/templates/mapper.xml")
                        .controller("/templates/controller.java")
                        .build())
                .execute();
    }
}
