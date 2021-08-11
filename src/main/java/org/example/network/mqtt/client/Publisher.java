package org.example.network.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

/**
 * MQTT Publisher function
 */
public class Publisher {
    MqttClient mqttClient = null;

    /**
     * Constructor
     * @param address: The IP:Port address
     * @throws MqttException: MQTT Exception
     */
    public Publisher(String address) throws MqttException {
       mqttClient = new MqttClient(address, MqttClient.generateClientId());
    }

    /**
     * Connect to broker
     * @throws MqttException: MQTT Exception
     */
    public void connect() throws MqttException {
        mqttClient.connect();
        System.out.println("publisher connection successful");
    }

    /**
     * Publish the message for only one topic to broker
     * @param topic: One topic that publisher would like to publish
     * @param payload: The message content
     * @throws MqttException: MQTT Exception
     */
    public void publish(String topic, String payload) throws MqttException{
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        mqttClient.publish(topic, mqttMessage);
    }

    /**
     * Publish the message for lots of topic to broker
     * @param topics: list of topics
     * @param payload: the message payload
     * @throws MqttException: MQTT Exception
     */
    public void publish(List<String> topics, String payload) throws MqttException{
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        for(String topic : topics){
            mqttClient.publish(topic, mqttMessage);
        }
    }

    /**
     * Disconnect the connection with broker
     * @throws MqttException: MQTT Exception
     */
    public void disconnect() throws MqttException{
        mqttClient.disconnect();
    }
}
