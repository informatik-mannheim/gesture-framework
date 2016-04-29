package hs_mannheim.gestureframework.messaging;

public interface IPacketReceiver {
   void receive(Packet packet);
   boolean accept(Packet.PacketType type);
}
