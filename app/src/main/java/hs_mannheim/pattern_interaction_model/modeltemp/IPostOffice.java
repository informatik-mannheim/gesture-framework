package hs_mannheim.pattern_interaction_model.modeltemp;

public interface IPostOffice extends IConnectionListener {
    void send(Packet packet);
    void register(IPacketReceiver receiver);
    void unregister(IPacketReceiver receiver);
    void receive(Packet packet);
}
