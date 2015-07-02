package hs_mannheim.pattern_interaction_model.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class ConnectThread extends Thread {
    private final String TAG = "[Bluetooth Connect Thread]";

    private final BluetoothSocket _socket;
    private final BluetoothChannel mChannel;
    private final BluetoothAdapter mBluetoothAdapter;

    public ConnectThread(BluetoothDevice device, BluetoothChannel channel, BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
        mChannel = channel;

        BluetoothSocket tmp = null;

        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createInsecureRfcommSocketToServiceRecord(BluetoothChannel.MY_UUID);
        } catch (IOException e) {
            Log.d(TAG, "Could not connect");
        }
        _socket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // block
            _socket.connect();
        } catch (IOException connectException) {
            Log.d(TAG, String.format("Could not connect: %s", connectException.getMessage()));

            this.cancel();

            return;
        }

        new ConnectedThread(_socket, mChannel).start();
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            _socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing socket");
        }
    }
}


