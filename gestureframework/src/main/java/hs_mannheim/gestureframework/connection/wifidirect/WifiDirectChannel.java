package hs_mannheim.gestureframework.connection.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import hs_mannheim.gestureframework.connection.LogActionListener;
import hs_mannheim.gestureframework.model.IConnection;
import hs_mannheim.gestureframework.model.IConnectionListener;
import hs_mannheim.gestureframework.model.Packet;

public class WifiDirectChannel extends BroadcastReceiver implements IConnection,
        WifiP2pManager.ConnectionInfoListener {
    private static final int MSG_DATA_RECEIVED = 0xAA;
    private static final int MSG_CONNECTION_ESTABLISHED = 0xBB;
    private static final int MSG_CONNECTION_LOST = 0xCC;

    private final String TAG = "[WifiP2P Channel]";
    private final IntentFilter mIntentFilter;
    private final Handler _handler;

    public WifiP2pManager mManager;
    public WifiP2pManager.Channel mChannel;

    private boolean mIsConnected;
    private IConnectionListener mListener;
    private ConnectedThread mConnectionThread;

    public WifiDirectChannel(WifiP2pManager manager,
                             WifiP2pManager.Channel channel,
                             Context context) {
        this.mManager = manager;
        this.mChannel = channel;

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        //TODO: This crashes when containing activity is paused
        context.registerReceiver(this, mIntentFilter);

        this._handler = createListenerHandler();
    }

    private Handler createListenerHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_DATA_RECEIVED:
                        mListener.onDataReceived((Packet) message.obj);
                        break;
                    case MSG_CONNECTION_ESTABLISHED:
                        mListener.onConnectionEstablished();
                        break;
                    case MSG_CONNECTION_LOST:
                        mListener.onConnectionLost();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    @Override
    public void register(IConnectionListener listener) {
        this.mListener = listener;
    }

    @Override
    public void disconnect() {
        disconnected();
    }

    @Override
    public void connect(String address) {
        if (isConnected()) return;

        // Right after a redirect, this does not work. For some reason the WifiP2pConfig is regarded
        // as invalid by the Android framework. This can be solved by calling discoverPeers() to
        // refresh the peers list, but that brings other problems. I don't really know how to fix this...

        final WifiP2pConfig config = new WifiP2pConfig();
        config.groupOwnerIntent = -1;
        config.wps.setup = WpsInfo.PBC;
        config.deviceAddress = address;

        // The result will be the WIFI_P2P_CONNECTION_CHANGED_ACTION broadcast event
        // for which we have to be registered.
        mManager.connect(mChannel, config, new LogActionListener(TAG,
                "Wifi P2P Connection established.",
                "Failed to establish Wifi P2P Connection"));
    }

    /**
     * The Broadcast event WIFI_P2P_CONNECTION_CHANGED_ACTION was received, so we know that either a
     * P2P group was formed or closed, depending on the value of the EXTRA_NETWORK_INFO.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected() && !isConnected()) {
                mManager.requestConnectionInfo(mChannel, this);
                Log.d(TAG, "P2P connection established, trying to connect");
            } else {
                Log.d(TAG, "P2P Connection closed.");
                disconnect();
            }
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (info.groupFormed && info.isGroupOwner) { // group owner starts the server
            Log.d(TAG, "Connection Info Changed: Connected [Client]");
            new Server(8090, this).execute();
        } else if (info.groupFormed) { // and the other party connects to it
            Log.d(TAG, "Connection Info Changed: Connected [Server]");
            new Client(info.groupOwnerAddress, 8090, this).execute();
        }
    }

    @Override
    public boolean isConnected() {
        return this.mIsConnected;
    }

    /**
     * Close all sockets and tear down the P2P connection.
     */
    public void disconnected() {
        if (!isConnected()) {
            return;
        }

        mIsConnected = false;

        Log.d(TAG, "disconnecting");

        // this will close the socket, NOT the P2P connection
        mConnectionThread.cancel();
        mConnectionThread = null;

        closeP2PConnection();

        _handler.obtainMessage(MSG_CONNECTION_LOST).sendToTarget();
    }

    /**
     * This closes the P2P Connection. After some research, this seems to be the proper way to do it.
     * Before closing a group the group information should be refreshed by calling requestGroupInfo().
     * The next step is to call removeGroup(), but only if we are the group owner. This properly
     * tears down the connection on both sides and will fire onConnectionChanged on both devices.
     */
    private void closeP2PConnection() {
        Log.d(TAG, "Closing P2P connection...");
        if (mManager != null && mChannel != null) {
            mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && mManager != null && mChannel != null && group.isGroupOwner()) {
                        mManager.removeGroup(mChannel, new LogActionListener(TAG, "P2P Group removed", "P2P Group NOT removed"));
                    }
                }
            });
        }
    }

    public void connected(ConnectedThread connectionThread) {
        mIsConnected = true;

        Log.d(TAG, "connecting");

        mConnectionThread = connectionThread;
        _handler.obtainMessage(MSG_CONNECTION_ESTABLISHED).sendToTarget();
    }

    public void receive(Object data) {
        _handler.obtainMessage(MSG_DATA_RECEIVED, data).sendToTarget();
    }

    @Override
    public void transfer(Packet packet) {
        if (isConnected()) {
            mConnectionThread.write(packet);
        }
    }
}
