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
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "[WifiP2P Server]";
    private final int mPort;
    private final WifiDirectChannel _channel;

    public Server(int port, WifiDirectChannel channel) {
        this.mPort = port;
        this._channel = channel;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(mPort);

            // blocks until anyone connects
            Socket client = serverSocket.accept();

            new ConnectedThread(client, _channel).start();

            serverSocket.close();

            return null;

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
