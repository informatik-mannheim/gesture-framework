package hs_mannheim.pattern_interaction_model;

import hs_mannheim.pattern_interaction_model.Model.*;

public class NoConnection implements hs_mannheim.pattern_interaction_model.Model.IConnection {
    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void transfer(String data, OnTransferDoneListener listener) {

    }

    @Override
    public void transfer(String data) {

    }

    @Override
    public void register(ConnectionListener listener) {

    }

    @Override
    public void connect(String address) {

    }
}
