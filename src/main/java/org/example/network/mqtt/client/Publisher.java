package org.example.network.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Publisher {
    MqttClient mqttClient = null;

    public Publisher(String address) throws MqttException {
       mqttClient = new MqttClient(address, MqttClient.generateClientId());
    }

    public void connect() throws MqttException {
        mqttClient.connect();
        System.out.println("connection successful");
    }

    public void publish(String topic, String payload) throws MqttException{
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        mqttClient.publish(topic, mqttMessage);
    }

    public void disconnect() throws MqttException{
        mqttClient.disconnect();
    }
}
