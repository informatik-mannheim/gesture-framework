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


