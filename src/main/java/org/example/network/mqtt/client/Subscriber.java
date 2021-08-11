package org.example.network.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT Subscriber function
 */
public class Subscriber {
    MqttClient mqttClient = null;
    List<JSONObject> queue = new ArrayList<>();

    /**
     * Constructor
     * @param address: The IP:Port address
     * @throws MqttException: MQTT Exception
     */
    public Subscriber(String address) throws MqttException{
        mqttClient = new MqttClient(address, MqttClient.generateClientId());
    }

    /**
     * Set the callback function
     * @param mqttCallback: The callback function for subscriber
     */
    public void setCallback(MqttCallback mqttCallback){
        mqttClient.setCallback(mqttCallback);
    }

    /**
     * Connect the broker
     * @throws MqttException: MQTT Exception
     */
    public void connect() throws MqttException {
        mqttClient.connect();
        System.out.println("subscriber connection successful");
    }

    /**
     * Subscriber only one topic
     * @param topic: one topic that a subscriber expects
     * @throws MqttException: MQTT Exception
     */
    public void subscribe(String topic) throws MqttException{
        mqttClient.subscribe(topic);
    }

    /**
     * Subscriber lots of topics
     * @param topics: over one topic
     * @throws MqttException: MQTT Exception
     */
    public void subscribe(String[] topics) throws MqttException{
        mqttClient.subscribe(topics);
    }

    /**
     * Put the message into queue
     * @param json: The message which formats JSON.
     */
    public void addQueue(JSONObject json){
        queue.add(json);
    }

    /**
     * Get the first element in queue
     * @return the first element in queue
     */
    public JSONObject getQueueFirstElement(){
        return queue.get(0);
    }

    /**
     * Check the queue is empty or not
     * @return Empty or not empty
     */
    public boolean isEmptyQueue(){
        return queue.isEmpty();
    }

    /**
     * Pop the first element in queue
     * @return the first element in queue
     */
    public JSONObject popQueue(){
        return queue.remove(0);
    }
}
