package hs_mannheim.gestureframework.model;

public interface IPacketReceiver {
   void receive(Packet packet);
   boolean accept(PacketType type);
}
