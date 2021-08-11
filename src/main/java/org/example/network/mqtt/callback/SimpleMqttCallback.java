package org.example.network.mqtt.callback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.network.mqtt.client.Subscriber;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Sensor callback in MQTT
 */
public class SimpleMqttCallback implements MqttCallback {
    private Subscriber subscriber = null;

    /**
     * Constructor
     * @param subscriber: The subscriber instance
     */
    public SimpleMqttCallback(Subscriber subscriber){
        this.subscriber = subscriber;
    }

    /**
     * (Callback) When connection is lost, then call
     * @param throwable: Connection lost exception
     */
    public void connectionLost(Throwable throwable) {
        throwable.printStackTrace();
        System.out.println("Connection to MQTT broker lost!");
    }

    /**
     * (Callback) When the message is arrived, then call
     * @param topic: The message maps to which topic
     * @param mqttMessage: The mqtt message
     */
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSS");
        JSONObject jsonObject = new JSONObject(new String(mqttMessage.getPayload()));
        jsonObject.put("endTime", ft.format(now));
        //System.out.println("Message received:\n\t"+ jsonObject.toString());
        subscriber.addQueue(jsonObject);
        //System.out.println("Message received:\n\t"+ new String(mqttMessage.getPayload()) );
    }

    /**
     * (Callback) Deliver completely
     * @param iMqttDeliveryToken: The delivery token
     */
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}
