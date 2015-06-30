package hs_mannheim.pattern_interaction_model.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/**
 * Thread that runs on both client and server device to send and receive data through streams.
 */
public class ConnectedThread extends Thread {
    private final String TAG = "[Bluetooth Connected Thread]";

    private final BluetoothSocket _socket;
    private BluetoothChannel _channel;
    private final InputStream mmInStream;
    private final OutputStream _outStream;

    public ConnectedThread(BluetoothSocket socket, BluetoothChannel channel) {
        _socket = socket;
        _channel = channel;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Could not acquire streams from socket");
        }

        mmInStream = tmpIn;
        _outStream = tmpOut;

        _channel.connected(this);
    }

    public void run() {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(mmInStream));

        while (true) {
            try {
                _channel.receive(stdIn.readLine());
            } catch (IOException e) {
                Log.d(TAG, "IO Exception: " + e.getMessage());
                this.cancel();

                break;
            }
        }
    }

    /**
     * Write data to socket through output stream.
     *
     * @param bytes to send
     */
    public void write(byte[] bytes) {
        try {
            _outStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Error sending data to remote device");
        }
    }

    /**
     * Close the socket and notify the channel about it.
     */
    public void cancel() {
        try {
            _socket.close();
            _channel.disconnected();
        } catch (IOException e) {
            Log.e(TAG, "Error closing client connection");
        }
    }
}
