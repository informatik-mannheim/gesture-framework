package hs_mannheim.pattern_interaction_model.Bluetooth;

public interface BluetoothListener {
    void onConnectionEstablished();
    void onDataReceived(String data);
    void onConnectionLost();
}
