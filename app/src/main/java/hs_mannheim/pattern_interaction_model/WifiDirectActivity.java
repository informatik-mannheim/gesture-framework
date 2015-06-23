package hs_mannheim.pattern_interaction_model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class WifiDirectActivity extends ActionBarActivity implements WifiActivity, AdapterView.OnItemClickListener {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;

    private IntentFilter mIntentFilter;

    private String connectedToAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this, (InteractionApplication) this.getApplicationContext());

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi_direct, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void discoverPeers(View view) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    @Override
    public void notify(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void listDevices(WifiP2pDeviceList peers) {
        ArrayList<String> strings = new ArrayList<>();

        ListView lv = (ListView) findViewById(R.id.listView);

        for(WifiP2pDevice device : peers.getDeviceList()) {

            if(connectedToAddress != null && connectedToAddress.equals(device.deviceAddress)) {
                strings.add(device.deviceName + "@" + device.deviceAddress + "(CONNECTED)");
            }
            else {
                strings.add(device.deviceName + "@" + device.deviceAddress);
            }
        }

        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings));

        lv.setOnItemClickListener(this);
    }

    @Override
    public void setConnectedDevice(String address) {
        this.connectedToAddress = address;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String address = ((TextView) view).getText().toString().split("@")[1];
        connect(address);
    }

    private void connect(final String address) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        config.groupOwnerIntent = 15;

        final WifiDirectActivity activity = this;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                activity.setConnectedDevice(address);

                Log.d("UXID", "Connected to other device");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("UXID", "Failed to connect");
                //failure logic
            }
        });
    }
}
