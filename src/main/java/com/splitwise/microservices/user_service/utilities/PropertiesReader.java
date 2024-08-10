package com.splitwise.microservices.user_service.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Service
public class PropertiesReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesReader.class);

    private static final Properties PROPERTIES;

    private static final String PROP_FILE = "keys.properties";

    private PropertiesReader() {
    }

    static {
        PROPERTIES = new Properties();
        final URL props = ClassLoader.getSystemResource(PROP_FILE);
        try {
            PROPERTIES.load(props.openStream());
        } catch (IOException ex) {
            LOGGER.info(ex.getClass().getName() + "PropertiesReader method");
        }
    }

    public static String getProperty(final String name) {

        return PROPERTIES.getProperty(name);
    }
}
