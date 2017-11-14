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

    /** Log tag for use logging info messages to LogCat */
    private static final String TAG = SocketService.class.getSimpleName();

    /** Constant used to match broadcast actions emitted from service to activity */
    public static final String ACTION_SOCKET_CONNECTION = MainActivity.class.getPackage()+".ACTION_SOCKET_CONNECTION";

    public static final String EXTRA_ORDER_RECEIVED = "EXTRA_ORDER_RECEIVED";
    public static final String EXTRA_CLIENT_CONNECTION = "EXTRA_CLIENT_CONNECTION";

    public static final String CLIENT_CONNECTED = "CLIENT_CONNECTED";
    public static final String CLIENT_DISCONNECTED = "CLIENT_DISCONNECTED";
    public static final String SERVER_CONNECTION_ERROR = "SERVER_CONNECTION_ERROR";


    /** Creating Binder given to clients */
    private final IBinder mBinder = new LocalBinder();

    /** Creating object to handle socket connection and events */
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

                    // Emitting client connection event to the activity
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    Log.i(TAG,"client connected");

                }

            }).on("order_received", new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Log.i(TAG,"server data: "+args[0]);

                    // bringing to from the activity if it is in background
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

                    // Emitting client disconnection event to the activity
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    Log.i(TAG,"client disconnected");

                }

            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Intent intent = new Intent(ACTION_SOCKET_CONNECTION);
                    intent.putExtra(EXTRA_CLIENT_CONNECTION,SERVER_CONNECTION_ERROR);

                    // Emitting connection server error to the activity
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    Log.i(TAG,"server connection error");

                    // If not disconnect the socket object, it keeps requesting connection
                    socket.disconnect();
                }

            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return mBinder;
    }

    /**
     * Methos that connects this client to socket server
     */
    public void connectSocket(){
        if(socket != null)
            socket.connect();
    }

    /**
     * Methos that disconnects this client from socket server
     */
    public void disconnectSocket(){
        if(socket != null)
            socket.disconnect();

    }

    /**
     * Class that returns service object to the activity
     *
     * This class is used to return a service object to the activity
     * for handle public service methods
     *
     * @author David Galvis
     */
    public class LocalBinder extends Binder {

        /**
         * This method returns an initialized object of this service
         *
         * @return SocketService object
         */
        SocketService getSocketService() {

            // Return this instance of LocalService so clients can call public methods
            return SocketService.this;
        }
    }
}
