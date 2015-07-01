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

    private final Socket mSocket;
    private final WifiDirectChannel mChannel;

    private final InputStream mInStream;
    private final OutputStream mOutStream;

    public ConnectedThread(Socket socket, WifiDirectChannel channel) {
        Log.d(TAG, "Connected Thread started");
        mSocket = socket;
        mChannel = channel;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Could not acquire streams from socket");
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;

        mChannel.connected(this);
    }

    public void run() {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(mInStream));

        while (true) {
            try {
                mChannel.receive(stdIn.readLine());
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
            mOutStream.write(bytes);
            //mOutStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error sending data to remote device");
        }
    }

    /**
     * Close the socket and notify the channel about it.
     */
    public void cancel() {
        try {
            mSocket.close();
            mChannel.disconnected();
        } catch (IOException e) {
            Log.e(TAG, "Error closing client connection");
        }
    }
}