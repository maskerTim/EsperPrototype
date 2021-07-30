package org.example;

import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.UpdateListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.engine.EsperEngine;
import org.example.listener.BloodPressureListener;
import org.example.listener.HeartAndBloodAbnormalListener;
import org.example.listener.HeartRateListener;
import org.example.listener.PulseOximeterListener;
import org.example.network.mqtt.callback.SimpleMqttCallback;
import org.example.network.mqtt.client.Subscriber;
import org.example.timer.task.SimpleTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

/**
 * Esper Prototype
 * Author: Masker Tim
 */
public class App 
{
    public static void main( String[] args )
    {
        /* read the environment configuration file */
        PropertyValues propertyValues = new PropertyValues();
        Properties properties = propertyValues.loadProperties();

        /* MQTT Subscriber setting */
        Subscriber subscriber = null;
        try {
            subscriber = new Subscriber(String.format("tcp://%s:%s", properties.get("MQTT_BROKER_IP"),
                    properties.get("MQTT_BROKER_PORT")));
            subscriber.setCallback(new SimpleMqttCallback(subscriber));
            subscriber.connect();
            subscriber.subscribe(String.format("%s", properties.get("MQTT_TOPIC")));

        }catch (MqttException ex){
            ex.printStackTrace();
        }

        /* Esper Implementation */
        EsperEngine esperEngine = EsperEngine.getInstance();
        Configuration configuration = new Configuration();

        URL resource = App.class.getClassLoader().getResource("HeartAndBloodAbnormalModule.epl");
        //File f = new File(String.valueOf(resource));

        esperEngine.createCompiler();
        try {
            //esperEngine.compile(f, configuration);
            esperEngine.compile(resource, configuration);
        }catch (EPCompileException | ParseException | IOException ex){
            // handle exception here
            throw new RuntimeException(ex);
        }
        esperEngine.createRuntime(configuration);
        try {
            esperEngine.deploy();
        }catch (EPDeployException ex){
            // handle exception here
            throw new RuntimeException(ex);
        }

//        List<UpdateListener> heartrate = new ArrayList<>();
//        heartrate.add(new HeartRateListener());
//        List<UpdateListener> bloodpressure = new ArrayList<>();
//        bloodpressure.add(new BloodPressureListener());
        List<UpdateListener> pulseoximeter = new ArrayList<>();
        pulseoximeter.add(new PulseOximeterListener());

//        esperEngine.setStatement("HeartRate", heartrate);
//        esperEngine.setStatement("BloodPressure", bloodpressure);
        esperEngine.setStatement("PulseOximeter", pulseoximeter);

        esperEngine.setListener();

        Timer timer = new Timer();
        timer.schedule(new SimpleTask(subscriber, esperEngine), 500, 1000);

        System.out.println( "Hello World!" );

    }
}
