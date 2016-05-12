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

package hs_mannheim.gestureframework.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class ConnectThread extends Thread {
    private final String TAG = "[Bt ConnectThread]";

    private final BluetoothSocket mBluetoothSocket;
    private final BluetoothChannel mChannel;
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothDevice mDevice;

    private static final int WAIT_TIME = 1000;
    private static int RETRIES = 3;

    public ConnectThread(BluetoothDevice device, BluetoothChannel channel, BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
        mChannel = channel;
        mDevice = device;

        mBluetoothAdapter.cancelDiscovery();

        BluetoothSocket tmp = null;

        try {
            tmp = mDevice.createRfcommSocketToServiceRecord(BluetoothChannel.MY_UUID);
        } catch (IOException e) {
            Log.d(TAG, "Could not connect");
        }
        mBluetoothSocket = tmp;
    }

    public void run() {
        try {
            // block
            mBluetoothSocket.connect();
        } catch (IOException connectException) {
            Log.e(TAG, String.format("Could not connect: %s", connectException.getMessage()));

            try {
                Log.d(TAG, String.format("Next try (%d left)", RETRIES));
                Thread.sleep(WAIT_TIME);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (RETRIES > 0) {
                new ConnectThread(mDevice, mChannel, mBluetoothAdapter).start();
                RETRIES--;
            } else {
                RETRIES = 3;
            }

            this.cancel();
            return;
        }

        new ConnectedThread(mBluetoothSocket, mChannel).start();
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            mBluetoothSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing socket");
        }
    }
}


