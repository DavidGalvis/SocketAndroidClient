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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for show data received from socket server
 *
 * This activity is used to display an object received from a socket server
 *
 * @author David Galvis
 */
public class MainActivity extends AppCompatActivity {

    /** Log tag for use logging info messages to LogCat */
    private static final String TAG = MainActivity.class.getSimpleName();

    Button btnConnect;
    Button btnDisconnect;
    TextView tvConectionStatus;
    TextView tvSubsidiary;
    LinearLayout llServerData;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /** Defined to handle service communication */
    SocketService mService;

    /** List of documents received from server messages. */
    private List<Document> mDocuments = new ArrayList<>();

    // handle if activity es bound to service
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
        tvSubsidiary = (TextView) findViewById(R.id.text_subsidiary);
        llServerData = (LinearLayout) findViewById(R.id.layout_server_data);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_document_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Binding data to recyclerView
        mAdapter = new DocumentsAdapter(mDocuments);
        mRecyclerView.setAdapter(mAdapter);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mService != null){

                    // Requesting service to start socket connection
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

                // Requesting service to stop socket connection
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

        // Binding this activity to service
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {

        // Registering broadcast to listen events emitted from service
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, new IntentFilter(SocketService.ACTION_SOCKET_CONNECTION));

        super.onResume();

        final Intent intent = getIntent();

        // Refreshing data with new data from server
        if(intent != null){
            String data = intent.getStringExtra(SocketService.EXTRA_ORDER_RECEIVED);
            setDataReceived(data);
        }
    }

    @Override
    protected void onPause() {

        // UnRegister broadcast to stop listening events emitted from service
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

        // Unbinding the service to this activity
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Method that parse and set Json string in the layout views
     *
     * @param data Json string with data from server
     */
    public void setDataReceived(String data){
        if(data != null){
            mDocuments.clear();
            JsonObject jsonData = new JsonParser().parse(data).getAsJsonObject();
            JsonArray jsonArray =jsonData.getAsJsonArray("documents");

            String mSubsidiaryName = jsonData.get("subsidiary").getAsJsonObject().get("name").getAsString();
            tvSubsidiary.setText(getString(R.string.title_subsidiary)+" "+mSubsidiaryName);

            for(int i=0 ; i<jsonArray.size() ; i++){
                String id = jsonArray.get(i).getAsJsonObject().get("id").getAsString();
                String name = jsonArray.get(i).getAsJsonObject().get("name").getAsString();
                String price = jsonArray.get(i).getAsJsonObject().get("precio").getAsString();
                Document document = new Document(id, name, price);
                mDocuments.add(document);
            }
            mAdapter.notifyDataSetChanged();
            llServerData.setVisibility(View.VISIBLE);

            Log.i(TAG,"count"+mDocuments.size());
        }
    }

    /**
     * receiver that listen events emitted from service.
     */
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
                    tvConectionStatus.setText(getString(R.string.msg_status_disconnected));
                    tvSubsidiary.setText("");
                    mDocuments.clear();
                    mAdapter.notifyDataSetChanged();
                    llServerData.setVisibility(View.INVISIBLE);
                }
                if(message != null && message.equals(SocketService.SERVER_CONNECTION_ERROR)){
                    tvConectionStatus.setText(getString(R.string.error_status_connection));
                }

            }
        }
    };
}
