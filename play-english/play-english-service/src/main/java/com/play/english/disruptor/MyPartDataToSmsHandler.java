package com.play.english.disruptor;

import com.lmax.disruptor.EventHandler;
import com.play.english.MyPartingDataEvent;

/**
 * @author chaiqx
 */
public class MyPartDataToSmsHandler implements EventHandler<MyPartingDataEvent> {

    @Override
    public void onEvent(MyPartingDataEvent myPartingDataEvent, long l, boolean b) throws Exception {
        long threadId = Thread.currentThread().getId();
        String carId = myPartingDataEvent.getCarId();
        System.out.println(String.format("thread %s part data into sms, carId = %s, seq = %s", threadId, carId, l));
    }
}
