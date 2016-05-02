package hs_mannheim.sysplace;

import android.content.Context;
import android.widget.Toast;

import hs_mannheim.gestureframework.model.ILifecycleListener;

public class ToastLifecycleListener implements ILifecycleListener {

    private Context mContext;

    public ToastLifecycleListener(Context context) {
        mContext = context;
    }

    @Override
    public void onConnect() { Toast.makeText(mContext, "CONNECT", Toast.LENGTH_SHORT).show(); }

    @Override
    public void onSelect() {
        Toast.makeText(mContext, "SELECT", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransfer() {
        Toast.makeText(mContext, "TRANSFER", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnect() {
        Toast.makeText(mContext, "DISCONNECT", Toast.LENGTH_SHORT).show();
    }
}
