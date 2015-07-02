package hs_mannheim.pattern_interaction_model.model;

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