package com.socket.david.socketandroidclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service {

    // setting string to log console messages
    private static final String TAG = SocketService.class.getSimpleName();
    public static final String ACTION_SOCKET_CONNECTION = MainActivity.class.getPackage()+".ACTION_SOCKET_CONNECTION";
    public static final String EXTRA_CLIENT_CONNECTION = "EXTRA_CLIENT_CONNECTION";
    public static final String CLIENT_CONNECTED = "CLIENT_CONNECTED";
    public static final String CLIENT_DISCONNECTED = "CLIENT_DISCONNECTED";
    public static final String SERVER_CONNECTION_ERROR = "SERVER_CONNECTION_ERROR";

    public static final String EXTRA_ORDER_RECEIVED = "EXTRA_ORDER_RECEIVED";

    // Creating Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    Socket socket;

    public SocketService() {}

    @Override
    public IBinder onBind(Intent intent) {

        try {
            Log.i(TAG, "service bound");

            socket = IO.socket(new URI("http://192.168.8.103:3000"));

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Intent intent = new Intent(ACTION_SOCKET_CONNECTION);
                    intent.putExtra(EXTRA_CLIENT_CONNECTION,CLIENT_CONNECTED);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    Log.i(TAG,"client connected");

                }

            }).on("order_received", new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Log.i(TAG,"server data: "+args[0]);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(EXTRA_ORDER_RECEIVED,args[0].toString());

                    startActivity(intent);
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Intent intent = new Intent(ACTION_SOCKET_CONNECTION);
                    intent.putExtra(EXTRA_CLIENT_CONNECTION,CLIENT_DISCONNECTED);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    Log.i(TAG,"client disconnected");
                }

            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Intent intent = new Intent(ACTION_SOCKET_CONNECTION);
                    intent.putExtra(EXTRA_CLIENT_CONNECTION,SERVER_CONNECTION_ERROR);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    Log.i(TAG,"server connection error");

                    socket.disconnect();
                }

            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return mBinder;
    }

    public void connectSocket(){
        if(socket != null)
            socket.connect();
    }

    public void disconnectSocket(){
        if(socket != null)
            socket.disconnect();

    }

    public class LocalBinder extends Binder {
        SocketService getSocketService() {

            // Return this instance of LocalService so clients can call public methods
            return SocketService.this;
        }
    }
}
