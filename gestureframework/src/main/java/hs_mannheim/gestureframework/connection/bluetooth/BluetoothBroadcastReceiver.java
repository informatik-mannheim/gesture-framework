package hs_mannheim.gestureframework.connection.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import hs_mannheim.gestureframework.model.InteractionApplication;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "[Bt Broadcast Receiver]";

    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothManager manager = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE));

        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            Log.d(TAG, "Found " + device.getAddress());

            if (device.getName() != null && device.getName().contains("-sysplace-")) {
                ConnectionInfo info = ConnectionInfo.from(manager.getAdapter().getName(),
                        device.getName(),
                        device.getAddress());

                // TODO: kind of a hack - maybe move it to the SysplaceContext
                ((InteractionApplication) context
                        .getApplicationContext())
                        .getSysplaceContext()
                        .getConnection()
                        .connect(info);
            }
        }
    }
}
