package hs_mannheim.sysplace;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;

import hs_mannheim.gestureframework.connection.bluetooth.ConnectionInfo;

public class BluetoothPairingService extends IntentService {

    private final String TAG = "[Bt Pairing Service]";
    private BluetoothAdapter mBluetoothAdapter;
    private String mOldName;
    private String mCurrentName;

    public BluetoothPairingService() {
        super("Bluetooth Pairing Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }

        setDiscoverable();
        mBluetoothAdapter.startDiscovery();
    }


    private void setDiscoverable() {
        if (!(mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(discoverableIntent);
        }
    }
}
