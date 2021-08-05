package org.example.network.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

public class Publisher {
    MqttClient mqttClient = null;

    public Publisher(String address) throws MqttException {
       mqttClient = new MqttClient(address, MqttClient.generateClientId());
    }

    /* connect by MQTT */
    public void connect() throws MqttException {
        mqttClient.connect();
        System.out.println("connection successful");
    }

    /* Publish single topic */
    public void publish(String topic, String payload) throws MqttException{
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        mqttClient.publish(topic, mqttMessage);
    }

    /* Publish multiple topics */
    public void publish(List<String> topics, String payload) throws MqttException{
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        for(String topic : topics){
            mqttClient.publish(topic, mqttMessage);
        }
    }

    public void disconnect() throws MqttException{
        mqttClient.disconnect();
    }
}
