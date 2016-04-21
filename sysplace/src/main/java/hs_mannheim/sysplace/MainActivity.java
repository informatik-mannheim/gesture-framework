package hs_mannheim.sysplace;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.graphics.Point;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import hs_mannheim.gestureframework.ConfigurationBuilder;
import hs_mannheim.gestureframework.InteractionApplication;
import hs_mannheim.gestureframework.connection.LogActionListener;
import hs_mannheim.gestureframework.connection.VoidActionListener;
import hs_mannheim.gestureframework.connection.wifidirect.WifiDirectChannel;
import hs_mannheim.gestureframework.model.GestureDetector;
import hs_mannheim.gestureframework.model.IPacketReceiver;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;

public class MainActivity extends AppCompatActivity implements IViewContext, GestureDetector.GestureEventListener, IPacketReceiver {

    private String TAG = "MAIN ACTIVITY";
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ConfigurationBuilder builder = new ConfigurationBuilder(getApplicationContext(), this);

        builder.withWifiDirect().swipe().buildAndRegister();

        ((InteractionApplication) getApplicationContext()).getInteractionContext().getGestureDetector().registerGestureEventListener(this);
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().register(this);

        // HACK
        mChannel = ((WifiDirectChannel) ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection()).mChannel;
        mManager = ((WifiDirectChannel) ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection()).mManager;

        registerLocalService();
        registerServiceRequest();
    }

    private void registerServiceRequest() {
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest, new LogActionListener(TAG, "Service Request added", "Service request could not be added. Turn on of Wifi, idiot."));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void registerLocalService() {
        mManager.clearLocalServices(mChannel, new LogActionListener(TAG, "services cleared", "services NOT cleared"));
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", new HashMap<String, String>());

        mManager.addLocalService(mChannel, serviceInfo, new LogActionListener(TAG, "Service registered", "Could not register service"));

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {
                ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection().connect(resourceType.deviceAddress);
                Log.d(TAG, "Service record available");
            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, null);
    }

    private void discoverService() {
        mManager.discoverServices(mChannel, new LogActionListener(TAG, "Service discovery started", "Service discovery NOT started."));
    }

    @Override
    public View getInteractionView() {
        return findViewById(R.id.layout_main);
    }

    @Override
    public Point getDisplaySize() {
        return null;
    }


    @Override
    public void onGestureDetected() {
        discoverService();
    }

    public void disconnect(View view) {
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection().disconnect();
    }

    @Override
    public void receive(Packet packet) {
        Log.d(TAG, "Packet received!");
        if(packet.getMessage().equals("Connection established")) {
            ((TextView) findViewById(R.id.textView)).setText("Connected");
        }
        else if(packet.getMessage().equals("Connection lost")) {
            ((TextView) findViewById(R.id.textView)).setText("NOT Connected");
        }
        else {
            Toast.makeText(this, packet.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean accept(PacketType type) {
        return true;
    }

    public void switchToConnectedActivity(View view){
        Intent intent = new Intent(this, ConnectedActivity.class);
        startActivity(intent);
    }

    public void ping(View view) {
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().send(new Packet("Ping!"));
    }
}
