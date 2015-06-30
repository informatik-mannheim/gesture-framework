package hs_mannheim.pattern_interaction_model.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "[WifiP2P Client]";
    private InetAddress host;
    private int port;
    private WifiDirectChannel _channel;

    public Client(InetAddress host, int port, WifiDirectChannel channel) {
        this.host = host;
        this.port = port;
        this._channel = channel;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "Client started");

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(this.host, this.port), 1000);

            new ConnectedThread(socket, _channel).start();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to client socket.");
        }

        return null;
    }
}
