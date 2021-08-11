package org.example.timer.task;

import org.example.engine.EsperEngine;
import org.example.network.mqtt.client.Subscriber;

import java.util.TimerTask;

/**
 * The task processing video events
 */
public class VideoTask extends TimerTask {
    private Subscriber subscriber = null;
    private EsperEngine esperEngine = null;

    /**
     * Constructor
     * @param subscriber: The subscriber instance
     * @param esperEngine: The esper engine instance
     */
    public VideoTask(Subscriber subscriber, EsperEngine esperEngine){
        this.subscriber = subscriber;
        this.esperEngine = esperEngine;
    }

    /**
     * Override the TimerTask function, and it sends events to esper engine
     */
    @Override
    public void run(){
        if(!subscriber.isEmptyQueue()) {
            System.out.println("send out to Esper!!");
            esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONPeopleNumberEvent");
        }
    }
}
