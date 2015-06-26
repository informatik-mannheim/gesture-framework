package hs_mannheim.pattern_interaction_model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;


public class BluetoothActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "[Bluetooth]";
    private int REQUEST_ENABLE_BT = 1;
    private final UUID MY_UUID = UUID.fromString("0566981a-1c02-11e5-9a21-1697f925ec7b");

    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mReceiver;
    private ArrayList<String> devices = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;


    private Button mStartDiscoveryButton;
    private ListView mDevicesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mStartDiscoveryButton = (Button) findViewById(R.id.btnStartDiscovery);
        mDevicesList = (ListView) findViewById(R.id.lvBluetoothDevices);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            mStartDiscoveryButton.setEnabled(false);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, devices);
        mDevicesList.setAdapter(mArrayAdapter);
        mDevicesList.setOnItemClickListener(this);

        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        mReceiver = new BluetoothDeviceFoundReceiver();
        registerReceiver(mReceiver, filter);
    }

    /*
        private List<String> findPairedDevices() {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices

            ArrayList<String> devices = new ArrayList<>();

            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }

            return devices;
        }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            mStartDiscoveryButton.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onResume();
        unregisterReceiver(mReceiver);
    }

    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    public void startDiscovery(View view) {
        mBluetoothAdapter.cancelDiscovery();
        makeDiscoverable();
        Log.d(TAG, "Discovery Started");
        mArrayAdapter.clear();
        mBluetoothAdapter.startDiscovery();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String device = mArrayAdapter.getItem(position);
        String address = device.split("\n")[1];
        BluetoothDevice deviceToConnect = mBluetoothAdapter.getRemoteDevice(address);
        Log.d(TAG, String.format("Device to connect to: %s", deviceToConnect));

        if (MainActivity.MODEL.equals("Nexus 4")) {
            // server
            new AcceptThread().run();
        } else {
            Log.d(TAG, "Connecting as client.");
            new ConnectThread(deviceToConnect).run();
        }
    }

    class BluetoothDeviceFoundReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceDescription = device.getName() + "\n" + device.getAddress();

                Log.d(TAG, String.format("Device discovered: %s", device.getName()));

                if (mArrayAdapter.getPosition(deviceDescription) == -1) {
                    mArrayAdapter.add(deviceDescription);
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "Could not connect");
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                if (!mmSocket.isConnected()) {
                    mmSocket.connect();
                }
            } catch (IOException connectException) {
                Log.d(TAG, String.format("Could not connect: %s", connectException.getMessage()));
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(mmSocket);
            ConnectedThread thread = new ConnectedThread(mmSocket);
            for(int i = 0; i < 10; i++) {
                thread.write("Hallo".getBytes());
                thread.write("Welt".getBytes());
            }
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("My App", MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)

                    new ConnectedThread(socket).run();
                    //manageConnectedSocket(socket);


                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI activity
                    Log.d(TAG, "obtained bytes:" + bytes);


                } catch (IOException e) {
                    Log.d(TAG, "IO Exception!");
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
