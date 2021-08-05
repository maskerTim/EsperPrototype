package org.example.operators;

import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.UpdateListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.engine.EsperEngine;
import org.example.engine.exceptions.NoListenersException;
import org.example.network.mqtt.client.Publisher;
import org.example.network.mqtt.client.Subscriber;
import org.example.operators.exceptions.NoTopicsException;
import org.example.timer.task.SimpleTask;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CEPOperator {
    private String name = null; // name of operator
    private List<String> subNetwork = null;
    private List<String> pubNetwork = null;
    private String[] subTopics = null;
    private List<String> pubTopics  = new ArrayList<>();
    private Subscriber subscriber = null;
    private Publisher publisher = null;
    private MqttCallback mqttCallback = null;
    private EsperEngine esperEngine = EsperEngine.getInstance();;
    private URL eplResource;
    private Map<String, List<UpdateListener>> statements = new HashMap<>();

    public CEPOperator(String name, List<String> sub, List<String> pub, URL eplResource){
        this.name = name;
        this.subNetwork = sub;
        this.pubNetwork = pub;
        this.eplResource = eplResource;
    }

    public void setEplResource(URL resource){
        this.eplResource = resource;
    }

    public void setSubTopics(String[] subTopics) {
        this.subTopics = subTopics;
    }

    public void setPubTopics(String[] pubTopics){
        this.pubTopics.addAll(Arrays.asList(pubTopics));
    }

    public void setCallback(MqttCallback mqttCallback){
        this.mqttCallback = mqttCallback;
    }

    public void setStatement(String statementName, List<UpdateListener> updateListeners){
        statements.put(statementName, updateListeners);
    }

    public String getName(){
        return name;
    }

    public Subscriber getSubscriber(){
        try{
            if(subscriber != null){
                return subscriber;
            }else {
                throw new NullPointerException();
            }
        }catch (NullPointerException e){
            System.out.println("Subscriber isn't still set.");
            e.printStackTrace();
            return null;
        }
    }

    public Publisher getPublisher(){
        try{
            if(publisher != null){
                return publisher;
            }else {
                throw new NullPointerException();
            }
        }catch (NullPointerException e){
            System.out.println("Publisher isn't still set.");
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getPubTopics(){
        try{
            if(pubTopics != null){
                return pubTopics;
            }else {
                throw new NullPointerException();
            }
        }catch (NullPointerException e){
            System.out.println("Publisher's Topic isn't still set.");
            e.printStackTrace();
            return null;
        }
    }

    public void createSubscriber() throws MqttException{
        subscriber = new Subscriber(String.format("tcp://%s:%s", subNetwork.get(0), subNetwork.get(1)));
    }

    public void createPublisher() throws MqttException{
        publisher = new Publisher(String.format("tcp://%s:%s", pubNetwork.get(0), pubNetwork.get(1)));
    }

    private void esperSetup() throws EPCompileException, ParseException,
            IOException, EPDeployException {
        Configuration configuration = new Configuration();
        esperEngine.createCompiler();
        esperEngine.compile(eplResource, configuration);
        esperEngine.createRuntime(configuration);
        esperEngine.deploy();
        try {
            for(String name : statements.keySet()){
                esperEngine.setStatement(name, statements.get(name));
            }
            esperEngine.setListener();
        }catch (NoListenersException ex){
            System.out.println("Listener is not set.");
            ex.printStackTrace();
        }
    }

    public void setup() throws NoTopicsException {
        if(subTopics.length == 0 || pubTopics.isEmpty()){
            throw new NoTopicsException();
        }else {
            try {
                subscriber.setCallback(mqttCallback);
                esperSetup();
            }catch (EPCompileException | ParseException | IOException | EPDeployException ex){
                System.out.println("Esper Setup Error occurs!!");
                ex.printStackTrace();
            }
        }
    }

    public void startup(TimerTask timerTask, int delay, int period){
        try {
            subscriber.connect();
            subscriber.subscribe(subTopics);
            publisher.connect();
        }catch (MqttException ex){
            System.out.println("MQTT Connection Error occurs!!!");
            ex.printStackTrace();
        }

        Timer timer = new Timer();
        timer.schedule(timerTask, delay, period);
    }

}
