package hs_mannheim.gestureframework.connection.bluetooth;

public class ConnectionInfo {

    private static final String DELIMITER = "-";
    public static ConnectionInfo INVALID_CONNECTION_INFO = new ConnectionInfo(null, false);

    private final String mMacAddress;
    private final boolean mIsServer;

    private ConnectionInfo(String macAddress, boolean isServer) {
        mMacAddress = macAddress;
        mIsServer = isServer;
    }

    public static ConnectionInfo from(String myBluetoothName,
                                      String otherBluetoothName,
                                      String otherMacAddress) {
        String[] myParts = myBluetoothName.split(DELIMITER);
        String[] otherParts = otherBluetoothName.split(DELIMITER);

        if (myParts.length < 3 || otherParts.length < 3) {
            return INVALID_CONNECTION_INFO;
        }

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
