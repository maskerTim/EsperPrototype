package org.example.listener;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

public class HeartAndBloodAbnormalListener implements UpdateListener {

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime){
        long id = (long) newEvents[0].get("ID");
        int heartrate = (int) newEvents[0].get("heartV");
        int systolic = (int) newEvents[0].get("bloodV");
        System.out.printf("ID: %d, Heart is over: %d; Blood is over: %d%n", id, heartrate, systolic);
    }
}
