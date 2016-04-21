package hs_mannheim.gestureframework.connection;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class LogActionListener implements WifiP2pManager.ActionListener {
    private final String mTag;
    private final String mSuccessMessage;
    private final String mFailMessage;

    public LogActionListener(String tag, String successMessage, String failMessage) {
        mTag = tag;
        mSuccessMessage = successMessage;
        mFailMessage = failMessage;
    }

    @Override
    public void onSuccess() {
        Log.d(mTag, mSuccessMessage);
    }

    @Override
    public void onFailure(int i) {
        Log.d(mTag, mFailMessage);
    }
}
