package hs_mannheim.pattern_interaction_model;

import android.app.Application;
import android.net.wifi.p2p.WifiP2pInfo;

public class InteractionApplication extends Application {
    private WifiP2pInfo p2pinfo;

    public WifiP2pInfo getP2pinfo() {
        return p2pinfo;
    }

    public void setP2pinfo(WifiP2pInfo p2pinfo) {
        this.p2pinfo = p2pinfo;
    }
}
