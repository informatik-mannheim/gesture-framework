package hs_mannheim.pattern_interaction_model.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import hs_mannheim.pattern_interaction_model.MainActivity;
import hs_mannheim.pattern_interaction_model.model.ConnectionListener;
import hs_mannheim.pattern_interaction_model.model.IConnection;

public class BluetoothChannel implements IConnection {

    private final int MSG_DATA_RECEIVED = 0x0A;
    private final int MSG_CONNECTION_ESTABLISHED = 0x0B;
    private final int MSG_CONNECTION_LOST = 0x0C;

    private final String TAG = "[BluetoothChannel]";
    private final UUID MY_UUID = UUID.fromString("0566981a-1c02-11e5-9a21-1697f925ec7b");

    private final Handler mHandler;
    private BluetoothDevice mConnectedDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectionListener mListener;

    private boolean isConnected = false;
    private ConnectedThread mConnectionThread;

    public BluetoothChannel(BluetoothAdapter bluetoothAdapter, ConnectionListener listener) {
        this.mBluetoothAdapter = bluetoothAdapter;
        mListener = listener;
        mHandler = createListenerHandler();
    }


    public BluetoothChannel(BluetoothAdapter bluetoothAdapter) {
        this.mBluetoothAdapter = bluetoothAdapter;
        mListener = null;
        mHandler = createListenerHandler();
    }

    private Handler createListenerHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                switch(message.what) {
                    case MSG_DATA_RECEIVED:
                        mListener.onDataReceived((String) message.obj);
                        break;
                    case MSG_CONNECTION_ESTABLISHED:
                        mListener.onConnectionEstablished();
                        break;
                    case MSG_CONNECTION_LOST:
                        mListener.onConnectionLost();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void register(ConnectionListener listener) {
        this.mListener = listener;
    }

    public String getConnectedDevice() {
        return this.isConnected() ? this.mConnectedDevice.getAddress() : "";
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public void transfer (String message) {
        if(isConnected) {
            mConnectionThread.write(message.getBytes());
        }
    }

    public void connect(String address) {
        if(isConnected()) return;

        BluetoothDevice deviceToConnect = mBluetoothAdapter.getRemoteDevice(address);
        this.mConnectedDevice = deviceToConnect;

        Log.d(TAG, String.format("Device to connect to: %s", deviceToConnect));

        if (MainActivity.MODEL.equals("Nexus 4")) {
            Log.d(TAG, "Connecting as server.");
            new AcceptThread(this).start();
        } else {
            Log.d(TAG, "Connecting as client.");
            new ConnectThread(deviceToConnect, this).start();
        }
    }

    private void receive(String data) {
        Log.d(TAG, "Data received: " + data);
        mHandler.obtainMessage(MSG_DATA_RECEIVED, data).sendToTarget();
    }

    private void connected(ConnectedThread connectionThread) {
        isConnected = true;
        this.mConnectionThread = connectionThread;
        mHandler.obtainMessage(MSG_CONNECTION_ESTABLISHED).sendToTarget();
    }


    private void disconnected() {
        this.isConnected = false;
        this.mConnectionThread = null;
        mHandler.obtainMessage(MSG_CONNECTION_LOST).sendToTarget();
    }

    public void close() {
        this.mConnectionThread.cancel();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothChannel mChannel;

        public ConnectThread(BluetoothDevice device, BluetoothChannel channel) {
            BluetoothSocket tmp = null;
            mChannel = channel;

            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "Could not connect");
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // block
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.d(TAG, String.format("Could not connect: %s", connectException.getMessage()));

                this.cancel();

                return;
            }

            new ConnectedThread(mmSocket, mChannel).start();
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket");
            }
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        private BluetoothChannel mChannel;

        public AcceptThread(BluetoothChannel channel) {
            this.mChannel = channel;

            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("My App", MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Could not open Server Socket");
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;

            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Error listening for incoming connections");
                    break;
                }

                if (socket != null) {
                    ConnectedThread connectedThread = new ConnectedThread(socket, mChannel);
                    connectedThread.start();

                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error connecting to client");
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Server Socket");
            }
        }
    }

    /**
     * Thread that runs on both client and server device to send and receive data through streams.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private BluetoothChannel mChannel;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, BluetoothChannel channel) {
            mmSocket = socket;
            mChannel = channel;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Could not acquire streams from socket");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            mChannel.connected(this);
        }

        public void run() {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(mmInStream));

            while (true) {
                try {
                    mChannel.receive(stdIn.readLine());
                } catch (IOException e) {
                    Log.d(TAG, "IO Exception: " + e.getMessage());
                    this.cancel();

                    break;
                }
            }
        }

        /**
         * Write data to socket through output stream.
         * @param bytes to send
         */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error sending data to remote device");
            }
        }

        /**
         * Close the socket and notify the channel about it.
         */
        public void cancel() {
            try {
                mmSocket.close();
                mChannel.disconnected();
            } catch (IOException e) {
                Log.e(TAG, "Error closing client connection");
            }
        }
    }

}
