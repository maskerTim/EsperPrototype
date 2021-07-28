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
            if(subscriber.getQueueFirstElement().get("dataType").equals("Heart Rate")) {
                esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONHeartAbnormalEvent");
                System.out.println("send Heart Beat!!");
            }else if(subscriber.getQueueFirstElement().get("dataType").equals("Blood Pressure")){
                esperEngine.sendEventJson(subscriber.popQueue().toString(), "JSONBloodAbnormalEvent");
                System.out.println("send Blood Pressure!!");
            }
            System.out.println("send out to Esper!!");
        }
    }
}
