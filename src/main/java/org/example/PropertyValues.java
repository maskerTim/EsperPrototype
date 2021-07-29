package org.example;

import org.example.App;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyValues {
    public Properties loadProperties(){
        String propFile = "config.properties";
        Properties properties = new Properties();
        System.out.println(PropertyValues.class.getClassLoader().getResourceAsStream(propFile));
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
