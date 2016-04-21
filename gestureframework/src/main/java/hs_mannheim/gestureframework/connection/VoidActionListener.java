package hs_mannheim.gestureframework.connection;

import android.net.wifi.p2p.WifiP2pManager;

public class VoidActionListener implements WifiP2pManager.ActionListener {
    @Override
    public void onSuccess() {
        // ignore
    }

    @Override
    public void onFailure(int i) {
        // ignore
    }
}
