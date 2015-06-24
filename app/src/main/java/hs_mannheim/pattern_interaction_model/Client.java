package hs_mannheim.pattern_interaction_model;

import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, Void> {

    private InetAddress host;
    private int port;

    public Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (this.host == null) {
            return null;
        }

        Socket socket = new Socket();

        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(params[0].getBytes());

            outputStream.close();

        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }

        return null;
    }
}
