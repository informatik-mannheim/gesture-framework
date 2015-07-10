package hs_mannheim.pattern_interaction_model.connection.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import hs_mannheim.pattern_interaction_model.model.IConnection;
import hs_mannheim.pattern_interaction_model.model.IConnectionListener;
import hs_mannheim.pattern_interaction_model.model.Packet;

public class WifiDirectChannel extends BroadcastReceiver implements IConnection,
        WifiP2pManager.ConnectionInfoListener {
    private static final int MSG_DATA_RECEIVED = 0xAA;
    private static final int MSG_CONNECTION_ESTABLISHED = 0xBB;
    private static final int MSG_CONNECTION_LOST = 0xCC;

    private final String TAG = "[WifiP2P Channel]";
    private final IntentFilter mIntentFilter;
    private final Handler _handler;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private boolean mIsConnected;
    private IConnectionListener mListener;
    private ConnectedThread mConnectionThread;
    private boolean mIsTryingToConnect;

    public WifiDirectChannel(WifiP2pManager manager, WifiP2pManager.Channel channel, Context context) {
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
    public void transfer(Packet packet) {
        Log.d(TAG, "Sending " + packet);

        if (isConnected()) {
            this.mConnectionThread.write(packet);
        }
    }

    @Override
    public void register(IConnectionListener listener) {
        this.mListener = listener;
    }

    @Override
    public void connect(String address) {
        if (isConnected()) return;

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        config.groupOwnerIntent = 15;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Wifi P2P Connection established.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Failed to establish Wifi P2P Connection.");
            }
        });
    }

    @Override
    public boolean isConnected() {
        return this.mIsConnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {
            onConnectionChanged(intent);
        }
    }

    /**
     * Important: this also changes when the other device asks to have a connection.
     */
    private void onConnectionChanged(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

        if (networkInfo.isConnected() && !mIsTryingToConnect && !isConnected()) { /* this only means p2p is connected */
            Log.d(TAG, "No socket open. Trying to connect");
            mIsTryingToConnect = true;
            mManager.requestConnectionInfo(mChannel, this);
        } else {
            this.disconnected();
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

    public void disconnected() {
        if (!isConnected()) {
            return;
        }

        mIsConnected = false;
        mIsTryingToConnect = false;
        mConnectionThread = null;
        mManager.removeGroup(mChannel, new IngoreActionListener());
        _handler.obtainMessage(MSG_CONNECTION_LOST).sendToTarget();
    }

    public void receive(Object data) {
        _handler.obtainMessage(MSG_DATA_RECEIVED, data).sendToTarget();
        Log.d(TAG, "Data received: " + data);
    }

    public void connected(ConnectedThread connectionThread) {
        mIsConnected = true;
        mIsTryingToConnect = false;
        this.mConnectionThread = connectionThread;
        _handler.obtainMessage(MSG_CONNECTION_ESTABLISHED).sendToTarget();
    }
}
