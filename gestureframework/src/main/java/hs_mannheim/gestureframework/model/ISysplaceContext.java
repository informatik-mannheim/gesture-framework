package hs_mannheim.gestureframework.model;

import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.Packet;

public interface ISysplaceContext {
    void select(Selection selection);
    void send(Packet packet);
    void registerForLifecycleEvents(ILifecycleListener lifecycleListener);
    void registerPacketReceiver(IPacketReceiver packetReceiver);
    void unregisterPacketReceiver(IPacketReceiver packetReceiver);
}
