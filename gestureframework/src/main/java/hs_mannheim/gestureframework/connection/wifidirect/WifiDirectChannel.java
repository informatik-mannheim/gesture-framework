package hs_mannheim.gestureframework.connection.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Random;

import hs_mannheim.gestureframework.connection.VoidActionListener;
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
    private WifiP2pConfig mConfig;

    private boolean mIsConnected;
    private IConnectionListener mListener;
    private ConnectedThread mConnectionThread;


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
        if (isConnected()) {
            mConnectionThread.write(packet);
        }
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

        mConfig = new WifiP2pConfig();
        mConfig.groupOwnerIntent = new Random().nextInt(14);
        mConfig.wps.setup = WpsInfo.PBC;
        mConfig.deviceAddress = address;

        mManager.connect(mChannel, mConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Wifi P2P Connection established.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Failed to establish Wifi P2P Connection (reason " + Integer.toString(reason) + ")");
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

        if (networkInfo.isConnected() && !isConnected()) { /* this only means p2p is connected */
            Log.d(TAG, "No socket open. Trying to connect");
            mManager.requestConnectionInfo(mChannel, this);
        } else {
            Log.d(TAG, "P2P Connection closed");
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

        Log.d(TAG, "disconnecting");

        mIsConnected = false;
        mConnectionThread.cancel();
        mConnectionThread = null;
        mManager.removeGroup(mChannel, new VoidActionListener());
        _handler.obtainMessage(MSG_CONNECTION_LOST).sendToTarget();
    }

    public void receive(Object data) {
        _handler.obtainMessage(MSG_DATA_RECEIVED, data).sendToTarget();
    }

    public void connected(ConnectedThread connectionThread) {
        Log.d(TAG, "connecting");

        mIsConnected = true;
        mConnectionThread = connectionThread;
        _handler.obtainMessage(MSG_CONNECTION_ESTABLISHED).sendToTarget();
    }
}
