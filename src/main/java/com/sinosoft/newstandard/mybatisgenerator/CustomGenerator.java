package com.sinosoft.newstandard.mybatisgenerator;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Eric
 **/
@Component
public class CustomGenerator {

    @Value("${mybatis.generator.generatorConfigFile}")
    private String generatorConfigFile;

    public void generate() throws InterruptedException, SQLException, IOException, InvalidConfigurationException, XMLParserException, XmlPullParserException {

        String rootDir = System.getProperty("user.dir")+  System.getProperty("file.separator");

        //mybatis-generator的配置文件
        String configPath = rootDir + generatorConfigFile;

        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        InputStream inputStream = new FileInputStream(configPath);
        Configuration config = cp.parseConfiguration(inputStream);
        Context generate = config.getContext("generate");

        /*设置生成类的目录*/

        //entity
        generate.getJavaModelGeneratorConfiguration().setTargetProject(rootDir);

        //mapper
        generate.getSqlMapGeneratorConfiguration().setTargetProject(rootDir);

        //dao
        generate.getJavaClientGeneratorConfiguration().setTargetProject(rootDir);

        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
}
