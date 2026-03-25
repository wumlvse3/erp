package com.jcloud.system.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    @Value("${springboot_path}")
    private String rootPath ;

    public static Properties loadProperties(String file) throws IOException {
        String configPath = "src/main/resources/"+file;

        Properties properties = new Properties();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(configPath));
        properties.load(bufferedReader);
        return properties;
    }
}
