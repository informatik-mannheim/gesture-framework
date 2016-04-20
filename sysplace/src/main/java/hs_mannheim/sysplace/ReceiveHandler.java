package hs_mannheim.sysplace;

import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.model.IPacketReceiver;
import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;

/**
 * Created by Dominick Madden on 20.04.2016.
 */
public class ReceiveHandler implements IPacketReceiver {

    GestureAnimation receiveAnimation;

    public ReceiveHandler(GestureAnimation receiveAnimation){
        this.receiveAnimation = receiveAnimation;
    }

    @Override
    public void receive(Packet packet) {

    }

    @Override
    public boolean accept(PacketType type) {
        return false;
    }
}
