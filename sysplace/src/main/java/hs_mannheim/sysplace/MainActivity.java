package hs_mannheim.sysplace;

import android.graphics.Point;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import hs_mannheim.gestureframework.ConfigurationBuilder;
import hs_mannheim.gestureframework.InteractionApplication;
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

        builder.withWifiDirect().bump().buildAndRegister();

        ((InteractionApplication) getApplicationContext()).getInteractionContext().getGestureDetector().registerGestureEventListener(this);
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().register(this);

        // HACK
        mChannel = ((WifiDirectChannel) ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection()).mChannel;
        mManager = ((WifiDirectChannel) ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection()).mManager;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRegistration();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void startRegistration() {
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("app", "sysplace");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
              Log.d(TAG, "Service registered");
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });


        // and for the discovery...

        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {
                Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
                Log.d(TAG, "Device address is " + device.deviceAddress);

                ((InteractionApplication) getApplicationContext()).getInteractionContext().getConnection().connect(device.deviceAddress);
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {
                Log.d(TAG, "Service record available");
                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
/*
                resourceType.deviceName = buddies
                        .containsKey(resourceType.deviceAddress) ? buddies
                        .get(resourceType.deviceAddress) : resourceType.deviceName;*/
            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Service Request Added");
                    }

                    @Override
                    public void onFailure(int code) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                });

    }



    private void discoverService() {


        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully started service discovery");
            }

            @Override
            public void onFailure(int code) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY

            }});
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
        Toast.makeText(this, packet.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean accept(PacketType type) {
        return true;
    }
}