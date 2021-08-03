package org.example.listener;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.network.mqtt.client.Publisher;

public class HeartRateListener implements UpdateListener {
    private Publisher publisher = null;
    private String topic = null;

    public HeartRateListener(Publisher publisher, String topic){
        this.publisher = publisher;
        this.topic = topic;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime){
        long id = (long) newEvents[0].get("ID");
        String startTime = (String) newEvents[0].get("startTime");
        String endTime = (String) newEvents[0].get("endTime");
        int heartrate = (int) newEvents[0].get("value");
        System.out.printf("start time: %s, end time: %s; ID: %d; Heart Rate is over: %d%n", startTime, endTime, id, heartrate);
        String signal = String.format("{\"ID\":\"%s\",\"value\":\"%s\"}", id, heartrate);
        try {
            publisher.publish(topic, signal);
            System.out.println("publish is successful.");
        }catch (MqttException ex){
            ex.printStackTrace();
        }
    }
}
