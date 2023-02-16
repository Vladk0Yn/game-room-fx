package com.yanovych.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {
    public static Properties getProperties(String name) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(name);
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }
}
