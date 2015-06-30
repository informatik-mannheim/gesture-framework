package hs_mannheim.pattern_interaction_model.model;

public class Selection {

    private Payload _payload;

    public Selection(Payload data) {
        setData(data);
    }

    public void updateSelection(Payload newData) {
        setData(newData);
    }

    public Payload getData() {
        return _payload;
    }

    private void setData(Payload data) {
        this._payload = data;
    }
}