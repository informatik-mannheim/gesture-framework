package hs_mannheim.gestureframework.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;

import android.os.Message;
import android.util.Log;

import java.util.UUID;

import hs_mannheim.gestureframework.model.IConnectionListener;
import hs_mannheim.gestureframework.model.IConnection;
import hs_mannheim.gestureframework.model.Packet;

public class BluetoothChannel implements IConnection {

    public final static UUID MY_UUID = UUID.fromString("0566981a-1c02-11e5-9a21-1697f925ec7b");
    private final int MSG_DATA_RECEIVED = 0x0A;
    private final int MSG_CONNECTION_ESTABLISHED = 0x0B;
    private final int MSG_CONNECTION_LOST = 0x0C;

    private final String TAG = "[BluetoothChannel]";

    private final Handler mHandler;
    private BluetoothDevice mConnectedDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private IConnectionListener mListener;

    private boolean mIsConnected = false;
    private ConnectedThread mConnectionThread;

    public BluetoothChannel(BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
        mListener = null;
        mHandler = createListenerHandler();
    }

    private Handler createListenerHandler() {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_DATA_RECEIVED:
                        mListener.onDataReceived((Packet) message.obj);
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

    public void register(IConnectionListener listener) {
        this.mListener = listener;
    }

    @Override
    public void disconnect() {
        // not implemented
    }

    public String getConnectedDevice() {
        return this.isConnected() ? this.mConnectedDevice.getAddress() : "";
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public void transfer(final Packet message) {
        if (mIsConnected) {
            mConnectionThread.write(message);
        }
    }

    public void connect(ConnectionInfo connectionInfo) {
        if(isConnected()) return;

        mConnectedDevice = mBluetoothAdapter.getRemoteDevice(connectionInfo.getMacAddress());

        Log.d(TAG, String.format("Device to connect to: %s", mConnectedDevice.getName()));

        if (connectionInfo.isServer()) {
            Log.d(TAG, "Connecting as server.");
            new AcceptThread(this, mBluetoothAdapter).start();
        } else {
            Log.d(TAG, "Connecting as client.");
            new ConnectThread(mConnectedDevice, this, mBluetoothAdapter).start();
        }
    }

    public void connect(String address) {
        if (isConnected()) return;
        
        mConnectedDevice = mBluetoothAdapter.getRemoteDevice(address);

        Log.d(TAG, String.format("Device to connect to: %s", mConnectedDevice));

        //TODO: Fix this; the whole accept / connect stuff sucks.
        if ("Nexus 4".equals("Nexus 4")) {
            Log.d(TAG, "Connecting as server.");
            new AcceptThread(this, mBluetoothAdapter).start();
        } else {
            Log.d(TAG, "Connecting as client.");
            new ConnectThread(mConnectedDevice, this, mBluetoothAdapter).start();
        }
    }

    public void receive(Packet data) {
        Log.d(TAG, "Data received: " + data);
        mHandler.obtainMessage(MSG_DATA_RECEIVED, data).sendToTarget();
    }

    public void connected(ConnectedThread connectionThread) {
        mIsConnected = true;
        this.mConnectionThread = connectionThread;
        mHandler.obtainMessage(MSG_CONNECTION_ESTABLISHED).sendToTarget();
    }


    public void disconnected() {
        this.mIsConnected = false;
        this.mConnectionThread = null;
        mHandler.obtainMessage(MSG_CONNECTION_LOST).sendToTarget();
    }

    public void close() {
        this.mConnectionThread.cancel();
    }
}
