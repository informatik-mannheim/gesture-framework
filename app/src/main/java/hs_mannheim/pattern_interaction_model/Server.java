package hs_mannheim.pattern_interaction_model;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<String, String, String> {

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("UXID", "Received String via Network:" + s);

    }

    @Override
    protected String doInBackground(String... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            InputStream inputstream = client.getInputStream();
            String string = fromStream(inputstream);

            serverSocket.close();

            return string;

        } catch (IOException e) {
            Log.e("UXID", e.getMessage());
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
