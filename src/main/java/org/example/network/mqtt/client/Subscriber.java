package org.example.network.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Subscriber {
    MqttClient mqttClient = null;
    List<JSONObject> queue = new ArrayList<>();

    public Subscriber(String address) throws MqttException{
        mqttClient = new MqttClient(address, MqttClient.generateClientId());
    }

    public void setCallback(MqttCallback mqttCallback){
        mqttClient.setCallback(mqttCallback);
    }

    public void connect() throws MqttException {
        mqttClient.connect();
        System.out.println("connection successful");
    }

    public void subscribe(String topic) throws MqttException{
        mqttClient.subscribe(topic);
    }

    public void addQueue(JSONObject json){
        queue.add(json);
    }

    public JSONObject getQueueFirstElement(){
        return queue.get(0);
    }

    public boolean isEmptyQueue(){
        return queue.isEmpty();
    }

    public JSONObject popQueue(){
        return queue.remove(0);
    }
}
