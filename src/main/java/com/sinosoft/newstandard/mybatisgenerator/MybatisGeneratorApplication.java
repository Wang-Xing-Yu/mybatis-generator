package com.sinosoft.newstandard.mybatisgenerator;

import com.sinosoft.newstandard.mybatisgenerator.common.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MybatisGeneratorApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MybatisGeneratorApplication.class, args);
        CustomGenerator customGenerator = SpringContextHolder.getBean("customGenerator");
        customGenerator.generate();
    }
}
