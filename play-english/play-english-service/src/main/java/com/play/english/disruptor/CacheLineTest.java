package com.play.english.disruptor;

/**
 * @author chaiqx on 2019/7/2
 */
public class CacheLineTest {

    private static long[][] arr;

    private static final int SIZE = 4096 * 4096;

    public static void main(String[] args) {
        arr = new long[SIZE][];
        for (int i = 0; i < SIZE; i++) {
            arr[i] = new long[8];
            for (int j = 0; j < 8; j++) {
                arr[i][j] = i * j + 1;
            }
        }
        long sum = 0L;
        long start = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            for(int j=0;j<8;j++){
                sum += arr[i][j];
            }
        }
        System.out.println("sum = "+ sum + " and cost time = " + (System.currentTimeMillis() - start) + " ms");
        sum = 0L;
        start = System.currentTimeMillis();
        for(int i=0;i<8;i++){
            for(int j=0;j<SIZE;j++){
                sum+=arr[j][i];
            }
        }
        System.out.println("sum = "+ sum + " and cost time = " + (System.currentTimeMillis() - start) + " ms");
    }
}
