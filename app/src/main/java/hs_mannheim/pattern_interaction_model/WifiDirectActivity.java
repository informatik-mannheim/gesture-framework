package hs_mannheim.pattern_interaction_model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hs_mannheim.gestureframework.connection.wifidirect.WifiDirectChannel;
import hs_mannheim.gestureframework.model.IPacketReceiver;
import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;

public class WifiDirectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, IPacketReceiver {
    private String TAG = "[WifiDirectActivity]";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private WifiDirectChannel mConnection;

    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);


        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onPeersChanged();
            }
        };

        mConnection = (WifiDirectChannel) ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection();
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
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Peer discovery failed");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);

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
        this.mConnection.transfer(new Packet("TeST\n"));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String address = ((TextView) view).getText().toString().split("@")[1];

        this.mConnection.connect(address);
    }

    @Override
    public void receive(Packet packet) {
        Toast.makeText(this, packet.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean accept(PacketType type) {
        return true;
    }
}
