package hs_mannheim.pattern_interaction_model.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<Void, Void, Void> {

    public final static String ACTION_DATA_RECEIVED = "hs_mannheim.pattern_interaction_model.DATA_RECEIVED";
    private static final String TAG = "[WifiP2P Server]";
    private final int mPort;
    private final WifiDirectChannel _channel;

    public Server(int port, WifiDirectChannel channel) {
        this.mPort = port;
        this._channel = channel;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "Server started");

        try {
            ServerSocket serverSocket = new ServerSocket(mPort);

            // blocks
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
