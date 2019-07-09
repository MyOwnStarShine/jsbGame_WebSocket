package com.play.english.disruptor;

/**
 * @author chaiqx on 2019/7/2
 */
public class FalseSharingTest implements Runnable {

    private final static int NUM_THREAD = 8;
    private final static long ITERATIONS = 500L * 1000L * 1000L;
    private final int arrayIndex;
    private static final ValueWithPadding[] vas = new ValueWithPadding[NUM_THREAD];

    static {
        for (int i = 0; i < vas.length; i++) {
            vas[i] = new ValueWithPadding();
        }
    }

    public FalseSharingTest(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public static void runTest() throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREAD];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new FalseSharingTest(i));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Override
    public void run() {
        long i = ITERATIONS - 1;
        while (0 != --i) {
            vas[arrayIndex].value = i;
        }
    }

    public final static class ValueWithPadding {
        protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
        protected long p8, p9, p10, p11, p12, p13, p14, p15;
    }

    public final static class ValueWithoutPadding {
        protected volatile long value = 0L;
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        runTest();
        System.out.println("cos time = " + (System.currentTimeMillis() - start));
    }
}
