package hs_mannheim.pattern_interaction_model;

import android.net.wifi.p2p.WifiP2pDeviceList;

public interface WifiActivity {
    void notify(String message);
    void listDevices(WifiP2pDeviceList peers);
    void setConnectedDevice(String address);
}
