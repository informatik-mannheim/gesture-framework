package hs_mannheim.gestureframework.connection.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import hs_mannheim.gestureframework.messaging.Packet;

/**
 * Thread that runs on both client and server device to send and receive data through streams.
 */
public class ConnectedThread extends Thread {
    private final String TAG = "[Bluetooth ConnThread]";

    private final BluetoothSocket mSocket;
    private BluetoothChannel mChannel;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private ObjectOutputStream mObjectOutputStream;

    public ConnectedThread(BluetoothSocket socket, BluetoothChannel channel) {
        Log.d(TAG, "Establishing Connection.");
        mSocket = socket;
        mChannel = channel;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = mSocket.getInputStream();
            tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Could not acquire streams from socket");
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;

        mChannel.connected(this);
    }

    public void run() {

        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(mInStream);
        } catch (IOException e) {
            mChannel.disconnect();
            Log.e(TAG, "Error: " + e.getMessage());
        }

        while (true) {
            try {
                if (objectInputStream != null) {
                    mChannel.receive((Packet) objectInputStream.readObject());
                }
            } catch (IOException e) {
                Log.e(TAG, "IO Exception: " + e.getMessage());
                mChannel.disconnect();
                break;
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "ClassNotFoundException: " + e.getMessage());
                mChannel.disconnect();
                break;
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException: " + e.getMessage());
                mChannel.disconnect();
                break;
            }
        }
    }

    /**
     * Write data to socket through output stream.
     * @param message to send
     */
    public void write(Packet message) {
        try {

            if (mObjectOutputStream == null) {
                mObjectOutputStream = new ObjectOutputStream(mOutStream);
            }

            mObjectOutputStream.writeObject(message);
            mObjectOutputStream.reset();
        } catch (IOException e) {
            Log.e(TAG, "Error sending data to remote device");
        }
    }

    /**
     * Close the socket and notify the channel about it.
     */
    public void cancel() {
        try {
            if (mSocket != null && mSocket.isConnected()) {
                mSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing client connection");
        }
    }
}
