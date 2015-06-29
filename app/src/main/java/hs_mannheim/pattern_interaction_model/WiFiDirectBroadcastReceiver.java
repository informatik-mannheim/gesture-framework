package hs_mannheim.pattern_interaction_model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.net.InetAddress;

import hs_mannheim.pattern_interaction_model.Model.IConnection;

public class WiFiDirectBroadcastReceiver {

    private final InteractionApplication mApplicationContext;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiActivity mActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       WifiActivity activity, InteractionApplication applicationContext) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.mApplicationContext = applicationContext;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                onP2pStateChanged(intent);
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                onPeersChanged();
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                onConnectionChanged(intent, context);
            default:
                break;
        }
    }

    private void onP2pStateChanged(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            mActivity.notify("Wifi is enabled");
        } else {
            mActivity.notify("Wifi is NOT enabled");
        }
    }

    private void onPeersChanged() {
        Log.d("UXID", "New peers discovered");

        if (mManager != null) {
            mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    mActivity.listDevices(peers);
                }
            });
        }
    }

    private void onConnectionChanged(Intent intent, Context context) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

        if (networkInfo.isConnected()) {
            //mManager.requestConnectionInfo(mChannel, this);
            //mManager.requestGroupInfo(mChannel, this);
            Log.d("UXID", "Connection State changed: connected");
        } else {
            mActivity.setConnectedDevice(null);
            Log.d("UXID", "Connection State changed: disconnected");
        }
    }

    public void onGroupInfoAvailable(WifiP2pGroup group) {
        mActivity.notify(group.getClientList().toString());
        mActivity.notify("Connected to group: " + group.getNetworkName() + "(group owner: " + group.isGroupOwner() + ")");
    }
}