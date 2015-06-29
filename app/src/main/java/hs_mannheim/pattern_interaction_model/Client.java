package hs_mannheim.pattern_interaction_model;

import android.os.AsyncTask;
import android.util.Log;

import java.io.FileNotFoundException;
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
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Pre Execute");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG, "Post Execute");
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.d(TAG, "start sending");
        Socket socket = new Socket();

        try {
            Log.d(TAG, "Socket bound");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);
            Log.d(TAG, "Connected");
            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(params[0].getBytes());
            Log.d(TAG, "Sent stuff successfully");
            outputStream.close();

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error");
        } catch (IOException e) {
            Log.e(TAG, "Error");
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        Log.d(TAG, "ERROR");
                    }
                }
            }
        }

        return null;
    }
}
