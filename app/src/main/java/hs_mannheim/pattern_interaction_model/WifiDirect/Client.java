package hs_mannheim.pattern_interaction_model.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, Void> {

    private static final String TAG = "[WifiP2P Client]";
    private InetAddress host;
    private int port;

    public Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected Void doInBackground(String... params) {
        Socket socket = new Socket();

        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(params[0].getBytes());

            outputStream.close();

        } catch (IOException e) {
            Log.e(TAG, "Error");
        } finally {
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.d(TAG, "ERROR");
                }
            }
        }

        return null;
    }
}
