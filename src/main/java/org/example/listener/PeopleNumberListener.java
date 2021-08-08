package org.example.listener;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.network.mqtt.client.Publisher;

import java.util.List;

public class PeopleNumberListener implements UpdateListener {
    private Publisher publisher = null;
    private List<String> topics = null;

    public PeopleNumberListener(Publisher publisher, List<String> topic){
        this.publisher = publisher;
        this.topics = topic;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime){
        int number = (int) newEvents[0].get("number");
        System.out.printf("The number of people in video:%d", number);
        String signal = String.format("{\"number\":%d}", number);
        try {
            for(String topic : topics){
                publisher.publish(topic, signal);
            }
            System.out.println("publish is successful.");
        }catch (MqttException ex){
            ex.printStackTrace();
        }
    }
}
