package com.example.projecttest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.example.projecttest.communication.CoreRegisterCenter;
import com.example.projecttest.communication.coremanager.CoreManager;
import com.example.projecttest.communication.pay.IPayCore;

public class App extends Application {

    protected static App instance = null;

    protected Activity mMainActivity;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CoreRegisterCenter.registerCore();
        CoreManager.getCore(IPayCore.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        instance = this;
        super.attachBaseContext(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
