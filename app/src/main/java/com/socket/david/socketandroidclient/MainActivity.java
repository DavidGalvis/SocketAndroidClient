package com.socket.david.socketandroidclient;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // setting string to log console messages
    private static final String TAG = MainActivity.class.getSimpleName();

    Button btnConnect;
    Button btnDisconnect;
    TextView tvConectionStatus;
    TextView tvMessage;

    // Defined to handle service communication
    SocketService mService;

    // returns if the service is bound or not.
    boolean mBound = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = (Button) findViewById(R.id.button_connect);
        btnDisconnect = (Button) findViewById(R.id.button_disconnect);
        tvConectionStatus = (TextView) findViewById(R.id.text_connection_status);
        tvMessage = (TextView) findViewById(R.id.text_message);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mService != null){
                    mService.connectSocket();
                    tvConectionStatus.setText(getString(R.string.msg_status_connecting));
                    Log.i(TAG, "connecting socket");
                }else{
                    tvConectionStatus.setText(getString(R.string.error_status_connection));
                    Log.i(TAG, "error connecting socket");

                }
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mService.disconnectSocket();
                Log.i(TAG, "disconnecting socket");
            }
        });

        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {

                // bound to LocalService, cast the IBinder and get LocalService instance
                SocketService.LocalBinder binder = (SocketService.LocalBinder) service;

                mService = binder.getSocketService();;
                mBound = true;

                Log.i(TAG, "Service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;

                tvConectionStatus.setText(getString(R.string.msg_status_disconnected));
                Log.i(TAG, "Service disconnected");
            }
        };

        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, new IntentFilter(SocketService.ACTION_SOCKET_CONNECTION));

        super.onResume();

        final Intent intent = getIntent();

        if(intent != null){
            String data = intent.getStringExtra(SocketService.EXTRA_ORDER_RECEIVED);
            tvMessage.setText(data);
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                receiver);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(SocketService.ACTION_SOCKET_CONNECTION)) {

                String message = intent.getStringExtra(SocketService.EXTRA_CLIENT_CONNECTION);

                if(message != null && message.equals(SocketService.CLIENT_CONNECTED)){
                    btnConnect.setVisibility(View.GONE);
                    btnDisconnect.setVisibility(View.VISIBLE);
                    tvConectionStatus.setText(getString(R.string.msg_status_connected));
                }
                if(message != null && message.equals(SocketService.CLIENT_DISCONNECTED)){
                    btnConnect.setVisibility(View.VISIBLE);
                    btnDisconnect.setVisibility(View.GONE);
                    tvMessage.setText("");
                    tvConectionStatus.setText(getString(R.string.msg_status_disconnected));
                }
                if(message != null && message.equals(SocketService.SERVER_CONNECTION_ERROR)){
                    tvConectionStatus.setText(getString(R.string.error_status_connection));
                }

            }
        }
    };
}
