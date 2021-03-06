/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework.connection.wifidirect;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import hs_mannheim.gestureframework.messaging.Packet;

public class ConnectedThread extends Thread {
    private static final String TAG = "[WifiP2P ConnThread]";

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

    @Override
    public void run() {
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(mInStream);
        } catch (IOException e) {
            Log.e(TAG, "Error creating Input stream. Disconnecting.");
            mChannel.disconnect();
            return;
        }

        while (true) {
            try {
                Packet data = (Packet) objectInputStream.readObject();
                mChannel.receive(data);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                mChannel.disconnect();
                break;
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.toString());
                mChannel.disconnect();
                break;
            } catch (NullPointerException e) {
                Log.e(TAG, e.toString());
                mChannel.disconnect();
                break;
            } catch(Exception e) {
                Log.e(TAG, "weird exception");
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
            if (mObjectOutputStream == null) {
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

            if (!mSocket.isClosed()) {
                Log.d(TAG, "Closing Socket");
                mSocket.close();
            }
        } catch(SocketException ex) {
            Log.d(TAG, "Nasty Socker Exception");
        } catch (IOException e) {
            Log.e(TAG, "Error closing client connection");
        }
    }
}