package com.socket.david.socketandroidclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SocketService extends Service {

    // Creating Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    // setting string to log console messages
    private static final String TAG = SocketService.class.getSimpleName();

    public SocketService() {}

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public class LocalBinder extends Binder {
        SocketService getSocketService() {

            // Return this instance of LocalService so clients can call public methods
            return SocketService.this;
        }
    }
}
