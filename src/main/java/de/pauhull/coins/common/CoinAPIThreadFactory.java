package de.pauhull.coins.common;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CoinAPIThreadFactory implements ThreadFactory {

    @Getter
    private static AtomicInteger lastThreadID = new AtomicInteger(0);

    private String threadName;

    public CoinAPIThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(threadName + " Task #" + lastThreadID.incrementAndGet());
        return thread;
    }

}
