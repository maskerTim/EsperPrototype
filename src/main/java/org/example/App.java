package org.example;

import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.*;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.engine.EsperEngine;
import org.example.listener.HeartAndBloodAbnormalListener;
import org.example.listener.HeartRateListener;
import org.example.listener.PersonListenerTest;
import org.example.network.mqtt.callback.SimpleMqttCallback;
import org.example.network.mqtt.client.Subscriber;
import org.example.timer.task.SimpleTask;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Esper Prototype
 * Author: Masker Tim
 */
public class App 
{
    public static void main( String[] args )
    {
        Subscriber subscriber = null;
        try {
            subscriber = new Subscriber("tcp://192.168.0.99:1883");
            subscriber.setCallback(new SimpleMqttCallback(subscriber));
            subscriber.connect();
            subscriber.subscribe("Try/MQTT");

        }catch (MqttException ex){
            ex.printStackTrace();
        }

        EsperEngine esperEngine = EsperEngine.getInstance();
        Configuration configuration = new Configuration();

        File f = new File("/home/maskertim/EsperPrototype/src/main/java/org/example/events/HeartAndBloodAbnormalModule.epl");

        esperEngine.createCompiler();
        try {
            esperEngine.compile(f, configuration);
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

        List<UpdateListener> mystate = new ArrayList<>();
        mystate.add(new HeartRateListener());
        List<UpdateListener> mytest = new ArrayList<>();
        mytest.add(new HeartAndBloodAbnormalListener());

        esperEngine.setStatement("HeartRate", mystate);
        esperEngine.setStatement("HigherAbnormal", mytest);

        esperEngine.setListener();

        Timer timer = new Timer();
        timer.schedule(new SimpleTask(subscriber, esperEngine), 500, 1000);

        System.out.println( "Hello World!" );
    }
}
