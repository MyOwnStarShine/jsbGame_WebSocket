package com.play.english.service;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import com.play.english.MyPartingDataEvent;
import com.play.english.disruptor.MyPartDataInDbHandler;
import com.play.english.disruptor.MyPartDataToKafkaHandler;
import com.play.english.disruptor.MyPartDataToSmsHandler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chaiqx
 */
@Service
public class DisruptorProductService {

    private Disruptor<MyPartingDataEvent> disruptor;
    private static final int NUM = 10;

//    @PostConstruct
//    public void init() {
//        /*(1)单个生产者--多个消费者 消费者顺序依赖----- begin*/
//        try {
//            ExecutorService executor = Executors.newFixedThreadPool(4);
//            Disruptor<MyPartingDataEvent> disruptor1 = new Disruptor<MyPartingDataEvent>(new EventFactory<MyPartingDataEvent>() {
//                @Override
//                public MyPartingDataEvent newInstance() {
//                    return new MyPartingDataEvent();
//                }
//            }, 2048, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
//            MyPartDataInDbHandler myPartDataInDbHandler = new MyPartDataInDbHandler();
//            MyPartDataToKafkaHandler myPartDataToKafkaHandler = new MyPartDataToKafkaHandler();
//            MyPartDataToSmsHandler myPartDataToSmsHandler = new MyPartDataToSmsHandler();
//            EventHandler[] myEventHandler = new EventHandler[2];
//            myEventHandler[0] = myPartDataInDbHandler;
//            myEventHandler[1] = myPartDataToKafkaHandler;
//            disruptor = disruptor1;
//            EventHandlerGroup<MyPartingDataEvent> eventEventHandlerGroup = disruptor.handleEventsWith(myEventHandler);
//            eventEventHandlerGroup.then(myPartDataToSmsHandler);
//            disruptor.start();
//            CountDownLatch countDownLatch = new CountDownLatch(1);
//            executor.submit(new MyParkingDataEventPublisher());
//            countDownLatch.await();
//            disruptor.shutdown();
//            executor.shutdown();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        /*(1)单个生产者--多个消费者 消费者顺序依赖----- end*/
//    }

    private class MyParkingDataEventPublisher implements Runnable {
        @Override
        public void run() {
            final MyParkingDataEventTranslator myParkingDataEventTranslator = new MyParkingDataEventTranslator();
            try {
                for (int i = 0; i < NUM; i++) {
                    disruptor.publishEvent(myParkingDataEventTranslator);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyParkingDataEventTranslator implements EventTranslator<MyPartingDataEvent> {

        @Override
        public void translateTo(MyPartingDataEvent myPartingDataEvent, long l) {
            myPartingDataEvent.setCarId("京A-" + (int) (Math.random() * 100000));
        }
    }
}
