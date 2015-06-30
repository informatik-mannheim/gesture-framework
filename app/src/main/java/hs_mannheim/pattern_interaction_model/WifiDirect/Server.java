package hs_mannheim.pattern_interaction_model.wifidirect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import hs_mannheim.pattern_interaction_model.bluetooth.AsyncResponse;

public class Server extends AsyncTask<String, String, String> {

    public final static String ACTION_DATA_RECEIVED = "hs_mannheim.pattern_interaction_model.DATA_RECEIVED";
    private static final String TAG = "[WifiP2P Server]";
    private final int mPort;
    private final AsyncResponse response;

    public Server(int port, AsyncResponse response) {
        this.mPort = port;
        this.response = response;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.response.processFinish(s);
        Log.d(TAG, "Received String via Network:" + s);
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "Server started");

        try {
            ServerSocket serverSocket = new ServerSocket(mPort);
            Socket client = serverSocket.accept();

            InputStream inputstream = client.getInputStream();
            String string = fromStream(inputstream);

            serverSocket.close();
            new Server(mPort, this.response).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            return string;

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public static String fromStream(InputStream in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }
}
