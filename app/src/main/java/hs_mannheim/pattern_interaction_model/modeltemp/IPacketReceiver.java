package hs_mannheim.pattern_interaction_model.modeltemp;

public interface IPacketReceiver {
   void receive(Packet packet);
   boolean accept(String type);
}
