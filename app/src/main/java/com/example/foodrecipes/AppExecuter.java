package com.example.foodrecipes;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecuter {
    private static AppExecuter instance;

    public static AppExecuter getInstant() {
        if (instance == null)
            instance = new AppExecuter();
        return instance;
    }

    private final ScheduledExecutorService mNetworkIO = Executors.newScheduledThreadPool(3);

    public ScheduledExecutorService networkIo()
    {
        return mNetworkIO;
    }
}
