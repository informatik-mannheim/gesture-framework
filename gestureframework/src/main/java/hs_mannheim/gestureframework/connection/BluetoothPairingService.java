/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework.connection;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Service that starts device discovery to find other Bluetooth devices.
 * A broadcast receiver listening for ACTION_FOUND intents will receiver a notification when
 * devices are found.
 */
public class BluetoothPairingService extends IntentService {

    protected final String TAG = "[Bt Pairing Service]";
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothPairingService() {
        super("Bluetooth Pairing Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Starting discovery");
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