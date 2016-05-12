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
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * Accepts incoming connections and arborts after the first successful connect.
 *
 * @author Horst Schneider
 */
public class AcceptThread extends Thread {
    private final String TAG = "[Bluetooth AccThread]";
    private final BluetoothServerSocket mmServerSocket;
    private BluetoothChannel mChannel;

    public AcceptThread(BluetoothChannel channel, BluetoothAdapter bluetoothAdapter) {
        this.mChannel = channel;

        bluetoothAdapter.cancelDiscovery();

        BluetoothServerSocket tmp = null;

        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("SysplaceApp",
                    BluetoothChannel.MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Could not open Server Socket");
        }

        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket;

        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Error listening for incoming connections");
                break;
            }

            if (socket != null) {
                new ConnectedThread(socket, mChannel).start();

                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error connecting to client");
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
