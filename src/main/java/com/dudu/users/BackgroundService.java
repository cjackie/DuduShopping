package com.dudu.users;


import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by chaojiewang on 5/12/18.
 */
class BackgroundService extends ScheduledThreadPoolExecutor {
    private static BackgroundService instance = new BackgroundService();
    public static BackgroundService getInstance() {
        return instance;
    }
    private BackgroundService() {
        super(3);
    }
}
