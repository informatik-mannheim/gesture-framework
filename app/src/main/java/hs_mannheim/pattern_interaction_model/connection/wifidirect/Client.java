package hs_mannheim.pattern_interaction_model.connection.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {
    private final String TAG = "[WifiP2P Client]";
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
            socket.connect(new InetSocketAddress(this._host, this._port), 2000);

            new ConnectedThread(socket, _channel).start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error writing to client socket.");
            _channel.disconnected();
        }

        return null;
    }
}
