package hs_mannheim.pattern_interaction_model.model;

public interface IPacketReceiver {
   void receive(Packet packet);
   boolean accept(PacketType type);
}
