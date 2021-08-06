package org.example;

import com.espertech.esper.runtime.client.UpdateListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.engine.EsperEngine;
import org.example.listener.HeartRateListener;
import org.example.network.mqtt.callback.SimpleMqttCallback;
import org.example.network.mqtt.callback.VideoMqttCallback;
import org.example.operators.CEPOperator;
import org.example.operators.exceptions.NoTopicsException;
import org.example.timer.task.SimpleTask;
import org.example.timer.task.VideoTask;
import org.opencv.core.Core;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

        /*----- Sensor Version --------*/
//        List<String> subNetwork = new ArrayList<>();
//        List<String> pubNetwork = new ArrayList<>();
//        subNetwork.add("192.168.100.75");
//        subNetwork.add("1882");
//        pubNetwork.add("192.168.100.75");
//        pubNetwork.add("1881");
//        URL resource = App.class.getClassLoader().getResource("HeartAndBloodAbnormalModule.epl");
//
//        /* CEP Operator Section */
//        CEPOperator cepOp = new CEPOperator("Op1", subNetwork, pubNetwork, resource);
//        // Create the subscriber and publisher
//        try {
//            cepOp.createSubscriber();
//            cepOp.createPublisher();
//        }catch (MqttException ex){
//            System.out.println("Subscriber or Publisher is connection error.");
//            ex.printStackTrace();
//        }
//        // Set the configuration of subscriber and publisher
//        String[] subTopics = {"Try/HeartRate"};
//        cepOp.setSubTopics(subTopics);
//        String[] pubTopics = {"Try/HeartActuator"};
//        cepOp.setPubTopics(pubTopics);
//        cepOp.setCallback(new SimpleMqttCallback(cepOp.getSubscriber()));
//        // Set Esper listener configuration
//        List<UpdateListener> heartrate = new ArrayList<>();
//        heartrate.add(new HeartRateListener(cepOp.getPublisher(), cepOp.getPubTopics()));
//        cepOp.setStatement("HeartRate", heartrate);
//
//        // Initialize the CEP operator
//        try {
//            cepOp.setup();
//        }catch (NoTopicsException e){
//            System.out.println("No Subscriber's or Publisher's Topic");
//            e.printStackTrace();
//        }
//
//        // Run the CEP Operator
//        cepOp.startup(new SimpleTask(cepOp.getSubscriber(), EsperEngine.getInstance()), 500, 1000);

        /*------- Video Version -----*/
        System.load("/opt/opencv-4.5.3/build/lib/libopencv_java453.so");
        List<String> subNetwork = new ArrayList<>();
        List<String> pubNetwork = new ArrayList<>();
        subNetwork.add("192.168.100.75");
        subNetwork.add("1883");
        pubNetwork.add("192.168.100.75");
        pubNetwork.add("1881");
        URL resource = App.class.getClassLoader().getResource("HeartAndBloodAbnormalModule.epl");

        CEPOperator cepOp = new CEPOperator("Video1", subNetwork, pubNetwork, resource);
        // Create the subscriber and publisher
        try {
            cepOp.createSubscriber();
            cepOp.createPublisher();
        }catch (MqttException ex){
            System.out.println("Subscriber or Publisher is connection error.");
            ex.printStackTrace();
        }
        // Set the configuration of subscriber and publisher
        String[] subTopics = {"Try/Video"};
        cepOp.setSubTopics(subTopics);
        String[] pubTopics = {"Try/HeartActuator"};
        cepOp.setPubTopics(pubTopics);
        try {
            URI uri = App.class.getClassLoader().getResource("haarcascade_car.xml").toURI();
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            FileSystems.newFileSystem(uri, env);
            Path carCascadeFile = Paths.get(uri);
            String filepath = carCascadeFile.toString();
            System.out.println(filepath);
            cepOp.setCallback(new VideoMqttCallback(cepOp.getSubscriber(), filepath));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("file load is error.");
        }
        // Set Esper listener configuration
        List<UpdateListener> heartrate = new ArrayList<>();
        heartrate.add(new HeartRateListener(cepOp.getPublisher(), cepOp.getPubTopics()));
        cepOp.setStatement("HeartRate", heartrate);

        // Initialize the CEP operator
        try {
            cepOp.setup();
        }catch (NoTopicsException e){
            System.out.println("No Subscriber's or Publisher's Topic");
            e.printStackTrace();
        }

        // Run the CEP Operator
        cepOp.startup(new VideoTask(cepOp.getSubscriber(), EsperEngine.getInstance()), 500, 1000);

        System.out.println( "Hello World!" );

    }
}
