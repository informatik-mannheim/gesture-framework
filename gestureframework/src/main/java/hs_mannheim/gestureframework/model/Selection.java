package hs_mannheim.gestureframework.model;

import hs_mannheim.gestureframework.messaging.Packet;

public class Selection {

    private Packet _packet;

    public final static Selection Empty = new Selection(new Packet("Nothing selected."));

    public Selection(Packet data) {
        setData(data);
    }

    public void updateSelection(Packet newData) {
        setData(newData);
    }

    public Packet getData() {
        return _packet;
    }

    private void setData(Packet data) {
        this._packet = data;
    }
}