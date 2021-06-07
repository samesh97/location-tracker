package com.sba.xtrack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class BackgroundService extends Service
{
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(BackgroundService.this,"Service was stopped!",Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(BackgroundService.this,"Service was started!",Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
