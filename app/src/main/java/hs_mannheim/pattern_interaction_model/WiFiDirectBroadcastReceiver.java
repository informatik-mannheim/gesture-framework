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

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver implements
        WifiP2pManager.GroupInfoListener,
        WifiP2pManager.ConnectionInfoListener {

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

    @Override
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
            mManager.requestConnectionInfo(mChannel, this);
            mManager.requestGroupInfo(mChannel, this);
            Log.d("UXID", "Connection State changed: connected");
        } else {
            mActivity.setConnectedDevice(null);
            Log.d("UXID", "Connection State changed: disconnected");
        }
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        mApplicationContext.setP2pinfo(info);

        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress;

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            Log.d("UXID", "Starting file server");
            new Server(mApplicationContext).execute();

            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            Log.d("UXID", "I am the client");
            //new Client().sendString(info.groupOwnerAddress, 8888);
            // The other device acts as the client. In this case,
            // you'll want to create a client thread that connects to the group
            // owner.
        }
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        mActivity.notify(group.getClientList().toString());
        mActivity.notify("Connected to group: " + group.getNetworkName() + "(group owner: " + group.isGroupOwner() + ")");
    }
}