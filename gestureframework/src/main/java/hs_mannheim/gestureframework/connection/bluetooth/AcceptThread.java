package hs_mannheim.gestureframework.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class AcceptThread extends Thread {
    private final String TAG = "[Bluetooth AccThread]";
    private final BluetoothServerSocket mmServerSocket;
    private BluetoothChannel mChannel;

    public AcceptThread(BluetoothChannel channel, BluetoothAdapter bluetoothAdapter) {
        this.mChannel = channel;

        bluetoothAdapter.cancelDiscovery();

        BluetoothServerSocket tmp = null;

        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("SysplaceApp", BluetoothChannel.MY_UUID);
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
                ConnectedThread connectedThread = new ConnectedThread(socket, mChannel);
                connectedThread.start();

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

    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing Server Socket");
        }
    }
}
