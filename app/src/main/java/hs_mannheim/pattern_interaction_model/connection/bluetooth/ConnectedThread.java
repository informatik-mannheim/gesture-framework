package hs_mannheim.pattern_interaction_model.connection.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import hs_mannheim.pattern_interaction_model.modeltemp.Packet;

/**
 * Thread that runs on both client and server device to send and receive data through streams.
 */
public class ConnectedThread extends Thread {
    private final String TAG = "[Bluetooth Connected Thread]";

    private final BluetoothSocket mSocket;
    private BluetoothChannel mChannel;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private ObjectOutputStream mObjectOutputStream;

    public ConnectedThread(BluetoothSocket socket, BluetoothChannel channel) {
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
            e.printStackTrace();
        }

        while (true) {
            try {
                mChannel.receive((Packet) objectInputStream.readObject());
            } catch (IOException e) {
                Log.d(TAG, "IO Exception: " + e.getMessage());
                this.cancel();

                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();

                break;
            }
        }
    }

    /**
     * Write data to socket through output stream.
     *
     * @param message to send
     */
    public void write(Packet message) {
        try {
            if(mObjectOutputStream == null) {
                mObjectOutputStream = new ObjectOutputStream(mOutStream);
            }
            mObjectOutputStream.writeObject(message);
        } catch (IOException e) {
            Log.e(TAG, "Error sending data to remote device");
        }
    }

    /**
     * Close the socket and notify the channel about it.
     */
    public void cancel() {
        try {
            if(mObjectOutputStream != null) {
                mObjectOutputStream.close();
            }

            mChannel.disconnected();
        } catch (IOException e) {
            Log.e(TAG, "Error closing client connection");
        }
    }
}
