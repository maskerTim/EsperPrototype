package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Get the configuration file, config.properties, of environment variable
 */
public class PropertyValues {
    /**
     * Load the environment variable file
     * @return key-value pairs of property variables
     */
    public Properties loadProperties(){
        String propFile = "config.properties";
        Properties properties = new Properties();
        System.out.println("properties file:"+PropertyValues.class.getClassLoader().getResourceAsStream(propFile));
        try(InputStream input = PropertyValues.class.getClassLoader().getResourceAsStream(propFile)){
            if(input != null){
                properties.load(input);
            }else {
                throw new FileNotFoundException("property file '"+propFile+"' not found in the classpath");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return properties;
    }
}
