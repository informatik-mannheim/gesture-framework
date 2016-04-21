package hs_mannheim.gestureframework.connection.wifidirect;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import hs_mannheim.gestureframework.model.Packet;

public class ConnectedThread extends Thread {
    private final String TAG = "[WifiP2P ConnThread]";

    private final Socket mSocket;
    private final WifiDirectChannel mChannel;

    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private ObjectOutputStream mObjectOutputStream;

    public ConnectedThread(Socket socket, WifiDirectChannel channel) {
        Log.d(TAG, "Creating ConnThread");
        mSocket = socket;
        mChannel = channel;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "streams not acquired");
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;

        mChannel.connected(this);
    }

    public void run() {
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(mInStream);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "some error");
            this.cancel();
            return;
        }

        while (true) {
            try {
                Packet data = (Packet) objectInputStream.readObject();
                mChannel.receive(data);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                this.cancel();
                break;
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.toString());
                this.cancel();
                break;
            } catch (NullPointerException e) {
                Log.e(TAG, e.toString());
                this.cancel();
                break;
            }
        }
    }

    /**
     * Write data to socket through output stream.
     *
     * @param packet to send
     */
    public void write(Packet packet) {
        try {
            if(mObjectOutputStream == null) {
                mObjectOutputStream = new ObjectOutputStream(mOutStream);
            }
            mObjectOutputStream.writeObject(packet);
        } catch (IOException e) {
            Log.e(TAG, "Error writing packet to stream");
        }

    }

    /**
     * Close the socket and notify the channel about it.
     */
    public void cancel() {
        try {
            mSocket.close(); // does this really help?

            if(mInStream != null) {
                mInStream.close();
                Log.d(TAG, "Closing InStream");
            }

            if(mOutStream != null) {
                mOutStream.close();
                Log.d(TAG, "Closing OutStream");
            }

            mChannel.disconnected();
        } catch (IOException e) {
            Log.e(TAG, "Error closing client connection");
        }
    }
}