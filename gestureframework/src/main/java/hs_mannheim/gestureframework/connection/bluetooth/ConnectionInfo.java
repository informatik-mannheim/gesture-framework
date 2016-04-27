package hs_mannheim.gestureframework.connection.bluetooth;

public class ConnectionInfo {

    private final String mMacAddress;
    private final boolean mIsServer;

    private ConnectionInfo(String macAddress, boolean isServer) {
        mMacAddress = macAddress;
        mIsServer = isServer;
    }

    public static ConnectionInfo from(String myBluetoothName, String otherBluetoothName, String otherMacAddress) {
        String[] myParts = myBluetoothName.split("-");
        String[] otherParts = otherBluetoothName.split("-");

        if(myParts.length != 3 || otherParts.length != 3) return null; // todo: make this suck less

        int myRandom = Integer.parseInt(myParts[2]);
        int otherRandom = Integer.parseInt(otherParts[2]);

        boolean server = myRandom < otherRandom;

        return new ConnectionInfo(otherMacAddress, server);
    }

    public String getMacAddress() {
        return mMacAddress;
    }
    public boolean isServer() {
        return mIsServer;
    }
}
