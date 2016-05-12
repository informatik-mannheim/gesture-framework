/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework.connection.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

public class ConnectionInfo implements Parcelable {

    private static final String DELIMITER = "-";
    public static ConnectionInfo INVALID_CONNECTION_INFO = new ConnectionInfo(null, false);

    private final String mMacAddress;
    private final boolean mIsServer;

    private ConnectionInfo(String macAddress, boolean isServer) {
        mMacAddress = macAddress;
        mIsServer = isServer;
    }

    public ConnectionInfo(Parcel in) {
        boolean[] booleans = new boolean[1];
        in.readBooleanArray(booleans);
        mMacAddress = in.readString();
        mIsServer = booleans[0];
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

    //
    // The Parcelable Stuff
    //

    public static final Creator<ConnectionInfo> CREATOR = new Creator<ConnectionInfo>() {
        @Override
        public ConnectionInfo createFromParcel(Parcel in) {
            return new ConnectionInfo(in);
        }

        @Override
        public ConnectionInfo[] newArray(int size) {
            return new ConnectionInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMacAddress());
        dest.writeBooleanArray(new boolean[] {isServer()});
    }
}
