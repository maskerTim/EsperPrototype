package org.example.timer.task;

import org.example.engine.EsperEngine;
import org.example.network.mqtt.client.Subscriber;

import java.util.TimerTask;

public class VideoTask extends TimerTask {
    private Subscriber subscriber = null;
    private EsperEngine esperEngine = null;

    public VideoTask(Subscriber subscriber, EsperEngine esperEngine){
        this.subscriber = subscriber;
        this.esperEngine = esperEngine;
    }

    @Override
    public void run(){
        if(!subscriber.isEmptyQueue()) {
            System.out.println("send out to Esper!!");
            esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONPeopleNumberEvent");
        }
    }
}
