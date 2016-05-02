package hs_mannheim.gestureframework.model;

import hs_mannheim.gestureframework.messaging.Packet;

/**
 * Represents the data selected to be transferred. Data is always encapsulated in a {@link Packet}
 * instance so it can be distributed through the  * {@link hs_mannheim.gestureframework.messaging.PostOffice}.
 */
@SuppressWarnings("unused")
public class Selection {

    private Packet _packet;

    public final static Selection Empty = new Selection(new Packet("Nothing selected."));

    public Selection(Packet data) {
        setData(data);
    }

    /**
     * Update the data of this {@link Selection} to a new {@link Packet}.
     * @param newPacket The new {@link Packet}.
     */
    public void updateSelection(Packet newPacket) {
        setData(newPacket);
    }

    /**
     * Get the data of this {@link Selection} as a {@link Packet}.
     */
    public Packet getData() {
        return _packet;
    }

    private void setData(Packet data) {
        this._packet = data;
    }
}