package hs_mannheim.gestureframework.model;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Random;

/**
 * Subclasses {@link Application} to provide a SysplaceContext that manages the Lifecycle of a
 * gesture enabled application. It also ensures that Bluetooth is enabled and the device naming
 * works properly.
 */
public class InteractionApplication extends Application {

    private static final String TAG = "[InteractionApp]";
    private SysplaceContext mSysplaceContext;
    private BluetoothAdapter mBluetoothAdapter;
    private String mOldName;
    private String mCurrentName;

    public SysplaceContext getSysplaceContext() {
        return mSysplaceContext;
    }

    public void setInteractionContext(SysplaceContext sysplaceContext) {
        mSysplaceContext = sysplaceContext;
        mSysplaceContext.setApplication(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();

        enableBluetooth();
        mOldName = mBluetoothAdapter.getName();
        mCurrentName = mOldName + "-sysplace-" + Integer.toString(new Random().nextInt(10000));
    }

    private void enableBluetooth() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }
    }

    public void toggleName(boolean newName) {
        Log.d(TAG, String.format("Renaming to %s", newName ? mCurrentName : mOldName));
        mBluetoothAdapter.setName(newName? mCurrentName : mOldName);
    }
}
