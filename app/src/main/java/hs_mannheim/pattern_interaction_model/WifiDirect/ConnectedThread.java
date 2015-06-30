package hs_mannheim.pattern_interaction_model.wifidirect;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectedThread extends Thread {
    private final String TAG = "[WifiP2P ConnectedThread]";

    private final Socket _socket;
    private final WifiDirectChannel _channel;

    private final InputStream _inStream;
    private final OutputStream _outStream;

    public ConnectedThread(Socket socket, WifiDirectChannel channel) {
        Log.d(TAG, "Connected Thread started");
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

        _inStream = tmpIn;
        _outStream = tmpOut;

        _channel.connected(this);
    }

    public void run() {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(_inStream));

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
            //_outStream.flush();
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