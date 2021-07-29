package org.example.timer.task;

import org.example.engine.EsperEngine;
import org.example.network.mqtt.client.Subscriber;

import java.util.TimerTask;

public class SimpleTask  extends TimerTask {
    private Subscriber subscriber = null;
    private EsperEngine esperEngine = null;

    public SimpleTask(Subscriber subscriber, EsperEngine esperEngine){
        this.subscriber = subscriber;
        this.esperEngine = esperEngine;
    }

    @Override
    public void run(){
        if(!subscriber.isEmptyQueue()) {
//            esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONHeartAbnormalEvent");
//            System.out.println("send Heart Beat!!");
//            esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONBloodAbnormalEvent");
//            System.out.println("send Blood Pressure!!");
            if(subscriber.getQueueFirstElement().get("dataType").equals("Pulse Oximeter")) {
                esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONPulseAbnormalEvent");
                System.out.println("send Pulse Oximeter!!");
            }
            System.out.println("send out to Esper!!");
        }
    }
}
