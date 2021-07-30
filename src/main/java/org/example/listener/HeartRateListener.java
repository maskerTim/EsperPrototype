package org.example.listener;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

import java.util.Date;

public class HeartRateListener implements UpdateListener {

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime){
        long id = (long) newEvents[0].get("ID");
        Date endTime = (Date) newEvents[0].get("endTime");
        int heartrate = (int) newEvents[0].get("value");
        System.out.printf("Time: %tc; ID: %d; Heart Rate is over: %d%n", endTime, id, heartrate);
    }
}
