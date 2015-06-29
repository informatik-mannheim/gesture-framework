package hs_mannheim.pattern_interaction_model.WifiDirect;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;

import hs_mannheim.pattern_interaction_model.AsyncResponse;
import hs_mannheim.pattern_interaction_model.Client;
import hs_mannheim.pattern_interaction_model.Model.ConnectionListener;
import hs_mannheim.pattern_interaction_model.Model.IConnection;
import hs_mannheim.pattern_interaction_model.Server;

public class WifiDirectChannel extends BroadcastReceiver implements IConnection,
        WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.GroupInfoListener,
        AsyncResponse {
    private final String TAG = "[WifiP2P Channel]";
    private final IntentFilter mIntentFilter;
    private int mPortOther = 0;
    private WifiP2pInfo p2pInfo = null;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private boolean isConnected;
    private ConnectionListener mListener;

    public WifiDirectChannel(WifiP2pManager manager, WifiP2pManager.Channel channel, Context context) {
        this.mManager = manager;
        this.mChannel = channel;

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Server.ACTION_DATA_RECEIVED);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        context.registerReceiver(this, mIntentFilter);
    }

    @Override
    public boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public void transfer(String data) {
        Log.d(TAG, "Sending " + data + " to " + p2pInfo.groupOwnerAddress + ":" + this.mPortOther);
        Client client = new Client(p2pInfo.groupOwnerAddress, this.mPortOther);
        client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);

    }

    @Override
    public void register(ConnectionListener listener) {
        this.mListener = listener;
    }

    @Override
    public void connect(String address) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        config.groupOwnerIntent = 15;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                isConnected = true;
                mListener.onConnectionEstablished();
                Log.d(TAG, "Connected to other device");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Failed to connect");
            }
        });
    }

    private void onConnectionChanged(Intent intent, Context context) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

        if (networkInfo.isConnected()) {
            mManager.requestConnectionInfo(mChannel, this);
            mManager.requestGroupInfo(mChannel, this);
            Log.d(TAG, "Connection State changed: connected");
        } else {
            Log.d(TAG, "Connection State changed: disconnected");
        }
    }


    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        this.p2pInfo = info;

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            Log.d(TAG, "Starting file server");
            new Server(8090, this).execute();
            mPortOther = 9090;

        } else if (info.groupFormed) {
            Log.d(TAG, "I am the client");
            new Server(9090, this).execute();
            mPortOther = 8090;
        }

        this.isConnected = true;
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        Log.d(TAG, "Connected to group: " + group.getNetworkName() + "(group owner: " + group.isGroupOwner() + ")");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case Server.ACTION_DATA_RECEIVED:
                mListener.onDataReceived("something received");
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                onConnectionChanged(intent, context);
            default:
                break;
        }
    }


    @Override
    public void processFinish(String output) {
        mListener.onDataReceived(output);
    }
}
