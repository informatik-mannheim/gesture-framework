package hs_mannheim.gestureframework.model;

public interface ISysplaceContext {
    void select(Selection selection);
    void send(Packet packet);
    void registerForLifecycleEvents(ILifecycleListener lifecycleListener);
    void registerPacketReceiver(IPacketReceiver packetReceiver);
    void unregisterPacketReceiver(IPacketReceiver packetReceiver);
}
