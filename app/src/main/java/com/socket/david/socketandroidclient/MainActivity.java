package com.socket.david.socketandroidclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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

                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                bindService(intent, mConnection, BIND_AUTO_CREATE);

                tvConectionStatus.setText(getString(R.string.msg_status_connecting));

                // TODO: Set connection socket logic
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mBound) {
                    unbindService(mConnection);
                    mBound = false;
                    btnConnect.setVisibility(View.VISIBLE);
                    btnDisconnect.setVisibility(View.GONE);
                    tvConectionStatus.setText(getString(R.string.msg_status_disconnected));

                    Toast.makeText(getApplicationContext(), "Service disconnected", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "disconnecting service");
                }
                // TODO: Set disconnect socket logic
            }
        });

        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {

                // bound to LocalService, cast the IBinder and get LocalService instance
                SocketService.LocalBinder binder = (SocketService.LocalBinder) service;

                mService = binder.getSocketService();

                mBound = true;
                btnConnect.setVisibility(View.GONE);
                btnDisconnect.setVisibility(View.VISIBLE);
                tvConectionStatus.setText(getString(R.string.msg_status_connected));

                Toast.makeText(getApplicationContext(), "Service connected", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;

                tvConectionStatus.setText(getString(R.string.msg_status_disconnected));
                Toast.makeText(getApplicationContext(), "Service disconnected", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Service disconnected");
            }
        };
    }
}
