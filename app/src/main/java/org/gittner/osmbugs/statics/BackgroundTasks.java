package org.gittner.osmbugs.statics;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundTasks extends ThreadPoolExecutor
{
    private static final BackgroundTasks instance = new BackgroundTasks();


    private BackgroundTasks()
    {
        super(
                4,
                8,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }


    public static BackgroundTasks getInstance()
    {
        return instance;
    }
}
