package org.example.timer.task;

import org.example.engine.EsperEngine;
import org.example.network.mqtt.client.Subscriber;

import java.util.TimerTask;

/**
 * The task that sends the received event to esper
 */
public class SimpleTask  extends TimerTask {
    private Subscriber subscriber = null;
    private EsperEngine esperEngine = null;

    /**
     * Constructor
     * @param subscriber: The subscriber instance
     * @param esperEngine: The esper engine instance
     */
    public SimpleTask(Subscriber subscriber, EsperEngine esperEngine){
        this.subscriber = subscriber;
        this.esperEngine = esperEngine;
    }

    /**
     * Override the TimerTask function, and it sends the event to esper engine
     */
    @Override
    public void run(){
        if(!subscriber.isEmptyQueue()) {
            if(subscriber.getQueueFirstElement().get("dataType").equals("Heart Rate")) {
                //System.out.println(Thread.currentThread().getName());
                System.out.println("send Heart Beat!!");
                esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONHeartAbnormalEvent");
            }
//            if(subscriber.getQueueFirstElement().get("dataType").equals("Blood Pressure")) {
//                System.out.println("send Blood Pressure!!");
//                esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONBloodAbnormalEvent");
//            }
//            if(subscriber.getQueueFirstElement().get("dataType").equals("Pulse Oximeter")) {
//                System.out.println("send Pulse Oximeter!!");
//                esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONPulseAbnormalEvent");
//            }
            System.out.println("send out to Esper!!");
        }
    }
}
