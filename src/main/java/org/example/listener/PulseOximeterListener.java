package org.example.listener;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

import java.util.Date;

public class PulseOximeterListener implements UpdateListener {

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPRuntime runtime){
        long id = (long) newEvents[0].get("ID");
        Date endTime = (Date) newEvents[0].get("endTime");
        int pulseoximeter = (int) newEvents[0].get("value");
        System.out.printf("Time: %tc; ID: %d; Pulse Oximeter is over: %d%n", endTime, id, pulseoximeter);
    }
}
