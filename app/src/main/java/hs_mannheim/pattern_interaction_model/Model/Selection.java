package hs_mannheim.pattern_interaction_model.model;

public class Selection {

    private String mData;

    public Selection() { }

    public Selection(String data) {
        setData(data);
    }

    public void updateSelection(String newSelection) {
        setData(newSelection);
    }

    public String getData() {
        return mData;
    }

    private void setData(String mData) {
        this.mData = mData;
    }
}