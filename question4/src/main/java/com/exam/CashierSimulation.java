package com.exam;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CashierSimulation {

    static final int CASHIERS = 9;
    static final double MEAN_ARRIVAL_MIN = 1.0;
    static final double MEAN_SERVICE_MIN = 10.0;
    static final int SIM_MINUTES = 8 * 60;
    static final long MS_PER_MINUTE = 10;

    static final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
    static final AtomicInteger waiting = new AtomicInteger();
    static final AtomicInteger maxQueue = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        ExecutorService cashiers = Executors.newFixedThreadPool(CASHIERS);
        for (int i = 0; i < CASHIERS; i++) {
            cashiers.submit(CashierSimulation::serve);
        }

        Random rnd = new Random();
        long end = System.currentTimeMillis() + (long) SIM_MINUTES * MS_PER_MINUTE;
        while (System.currentTimeMillis() < end) {
            sleepMinutes(exponential(rnd, MEAN_ARRIVAL_MIN));
            maxQueue.accumulateAndGet(waiting.incrementAndGet(), Math::max);
            queue.add(new Object());
        }

        cashiers.shutdownNow();
        cashiers.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Найбільша довжина черги: " + maxQueue.get());
    }

    static void serve() {
        Random rnd = new Random();
        try {
            while (true) {
                queue.take();
                waiting.decrementAndGet();
                sleepMinutes(exponential(rnd, MEAN_SERVICE_MIN));
            }
        } catch (InterruptedException ignored) {
        }
    }

    static double exponential(Random rnd, double mean) {
        return -mean * Math.log(1.0 - rnd.nextDouble());
    }

    static void sleepMinutes(double minutes) throws InterruptedException {
        long ms = Math.round(minutes * MS_PER_MINUTE);
        if (ms > 0) {
            Thread.sleep(ms);
        }
    }
}
