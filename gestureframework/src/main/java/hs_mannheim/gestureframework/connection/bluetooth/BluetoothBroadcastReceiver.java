package hs_mannheim.gestureframework.connection.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import hs_mannheim.gestureframework.model.InteractionApplication;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private String TAG = "[Bt Broadcast Receiver]";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Test");
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d(TAG, "Found " + device.getAddress());

            if (device.getName() != null && device.getName().contains("-sysplace-")) {
                BluetoothManager manager = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE));
                ConnectionInfo info = (ConnectionInfo.from(manager.getAdapter().getName(), device.getName(), device.getAddress()));

                // TODO: should be forbidden
                ((InteractionApplication) context.getApplicationContext()).getSysplaceContext().getConnection().connect(info);
            }
        }
    }
}
