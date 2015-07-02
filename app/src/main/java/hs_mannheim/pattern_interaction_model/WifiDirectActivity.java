package hs_mannheim.pattern_interaction_model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hs_mannheim.pattern_interaction_model.model.IConnectionListener;
import hs_mannheim.pattern_interaction_model.model.IPacketReceiver;
import hs_mannheim.pattern_interaction_model.model.IPostOffice;
import hs_mannheim.pattern_interaction_model.model.Packet;
import hs_mannheim.pattern_interaction_model.wifidirect.WifiDirectChannel;

public class WifiDirectActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, IConnectionListener, IPacketReceiver {
    private String TAG = "[WifiDirectActivity]";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private WifiDirectChannel mConnection;

    private IntentFilter mIntentFilter;
    private IPostOffice mPostOffice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mPostOffice = ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onPeersChanged();
            }
        };

        this.mConnection = new WifiDirectChannel(mManager, mChannel, this);
        this.mConnection.register(this);
    }

    private void onPeersChanged() {
        Log.d(TAG, "New peers discovered");

        if (mManager != null) {
            mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    listDevices(peers);
                }
            });
        }
    }

    public void discoverPeers(View view) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Peer discovery successful.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Peer discovery failed.");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        mPostOffice.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        mPostOffice.unregister(this);
    }

    public void listDevices(WifiP2pDeviceList peers) {
        ArrayList<String> strings = new ArrayList<>();

        ListView lv = (ListView) findViewById(R.id.listView);

        for (WifiP2pDevice device : peers.getDeviceList()) {
            strings.add(device.deviceName + "@" + device.deviceAddress);
        }

        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings));
        lv.setOnItemClickListener(this);
    }

    public void sendStuff(View view) {
        this.mConnection.transfer(new Packet("DATA", "TeST\n"));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String address = ((TextView) view).getText().toString().split("@")[1];
        Log.d(TAG, "Connecting to " + address);

        this.mConnection.connect(address);
    }

    @Override
    public void onConnectionLost() {
        Toast.makeText(this, "Connection lost", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionEstablished() {
        Toast.makeText(this, "Connection established", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataReceived(Packet packet) {
        /* handled by post office now */
    }

    @Override
    public void receive(Packet packet) {
        Toast.makeText(this, packet.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean accept(String type) {
        return true;
    }
}
