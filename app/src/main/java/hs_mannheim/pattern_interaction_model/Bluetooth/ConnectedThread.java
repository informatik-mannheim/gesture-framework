package hs_mannheim.pattern_interaction_model.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import hs_mannheim.pattern_interaction_model.model.Packet;


/**
 * Thread that runs on both client and server device to send and receive data through streams.
 */
public class ConnectedThread extends Thread {
    private final String TAG = "[Bluetooth Connected Thread]";

    private final BluetoothSocket _socket;
    private BluetoothChannel _channel;
    private final InputStream _inStream;
    private final OutputStream _outStream;
    private ObjectOutputStream _objectOutStream;

    public ConnectedThread(BluetoothSocket socket, BluetoothChannel channel) {
        _socket = socket;
        _channel = channel;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = _socket.getInputStream();
            tmpOut = _socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Could not acquire streams from socket");
        }

        _inStream = tmpIn;
        _outStream = tmpOut;

        _channel.connected(this);
    }

    public void run() {

        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(_inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                _channel.receive((Packet) objectInputStream.readObject());
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
            if(_objectOutStream == null) {
                _objectOutStream = new ObjectOutputStream(_outStream);
            }
            _objectOutStream.writeObject(message);
        } catch (IOException e) {
            Log.e(TAG, "Error sending data to remote device");
        }
    }

    /**
     * Close the socket and notify the channel about it.
     */
    public void cancel() {
        try {
            if(_objectOutStream != null) {
                _objectOutStream.close();
            }

            _channel.disconnected();
        } catch (IOException e) {
            Log.e(TAG, "Error closing client connection");
        }
    }
}
