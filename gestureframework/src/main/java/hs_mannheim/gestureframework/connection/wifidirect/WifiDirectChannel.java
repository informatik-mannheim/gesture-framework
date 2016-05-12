/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

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

import hs_mannheim.gestureframework.connection.bluetooth.ConnectionInfo;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.connection.IConnection;
import hs_mannheim.gestureframework.connection.IConnectionListener;

public class WifiDirectChannel extends BroadcastReceiver implements IConnection,
        WifiP2pManager.ConnectionInfoListener {
    private static final int MSG_DATA_RECEIVED = 0xAA;
    private static final int MSG_CONNECTION_ESTABLISHED = 0xBB;
    private static final int MSG_CONNECTION_LOST = 0xCC;

    private final String TAG = "[WifiP2P Channel]";
    private final Handler _handler;

    public WifiP2pManager mManager;
    public WifiP2pManager.Channel mChannel;

    private boolean mIsConnected;
    private IConnectionListener mListener;
    private ConnectedThread mConnectedThread;

    public WifiDirectChannel(WifiP2pManager manager,
                             WifiP2pManager.Channel channel,
                             Context context) {
        this.mManager = manager;
        this.mChannel = channel;

        IntentFilter p2pChangedIntentFilter = new IntentFilter();
        p2pChangedIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        //TODO: This crashes when containing activity is paused
        context.registerReceiver(this, p2pChangedIntentFilter);

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
        //TODO: This should allow more than one listener. Have a look at the Bluetooth Version.
        this.mListener = listener;
    }

    @Override
    public void unregister(IConnectionListener listener) {
        this.mListener = null;
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
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Wifi P2P Connection established.");
            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "Failed to establish Wifi P2P Connection");
            }
        });
    }

    @Override
    public void connect(ConnectionInfo connectionInfo) {
        // only works for bluetooth right now, have to change this anyways
    }

    /**
     * The Broadcast event WIFI_P2P_CONNECTION_CHANGED_ACTION was received, so we know that either a
     * P2P group was formed or closed, depending on the value of the EXTRA_NETWORK_INFO.
     *
     * @param context The {@link Context} from which the intent was send.
     * @param intent The intent that was received.
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
        mConnectedThread.cancel();
        mConnectedThread = null;

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
                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "P2P Group removed");
                            }

                            @Override
                            public void onFailure(int i) {
                                Log.d(TAG, "P2P Group NOT removed");
                            }
                        });
                    }
                }
            });
        }
    }

    public void connected(ConnectedThread connectionThread) {
        mIsConnected = true;

        Log.d(TAG, "connecting");

        mConnectedThread = connectionThread;
        _handler.obtainMessage(MSG_CONNECTION_ESTABLISHED).sendToTarget();
    }

    public void receive(Object data) {
        _handler.obtainMessage(MSG_DATA_RECEIVED, data).sendToTarget();
    }

    @Override
    public void transfer(Packet packet) {
        if (isConnected()) {
            mConnectedThread.write(packet);
        }
    }
}
