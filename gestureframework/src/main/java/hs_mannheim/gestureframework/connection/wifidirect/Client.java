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

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "[WifiP2P Client]";
    private InetAddress _host;
    private int _port;
    private WifiDirectChannel _channel;

    public Client(InetAddress host, int port, WifiDirectChannel channel) {
        this._host = host;
        this._port = port;
        this._channel = channel;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "Client started");

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(this._host, this._port), 5000);

            new ConnectedThread(socket, _channel).start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error writing to client socket.");
            _channel.disconnected();
        }

        return null;
    }
}
