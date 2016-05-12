package hs_mannheim.gestureframework.messaging;

import java.io.Serializable;

public class Packet implements Serializable {
    private final PacketType mPacketType;
    private final String mMessage;

    public Packet(String message) {
        mPacketType = PacketType.PlainString;
        mMessage = message;
    }

    protected Packet(PacketType packetType, String message) {
        mPacketType = packetType;
        mMessage = message;
    }

    public PacketType getType() {
        return mPacketType;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        return String.format("Message of type: %s containing: %s\n", mPacketType, mMessage);
    }

    public enum PacketType {
        PlainString, StitchSyn, StitchAck, BumpSyn, BumpAck, Image, ConnectionLost, ConnectionEstablished
    }
}