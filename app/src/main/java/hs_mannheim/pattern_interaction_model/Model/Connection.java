package hs_mannheim.pattern_interaction_model.model;

import android.content.Context;

public class Connection implements IConnection {

    private final Context mApplicationContext;

    public Connection(Context applicationContext) {
        this.mApplicationContext = applicationContext;
    }

    @Override
    public boolean isConnected() {
        return true; // for now
    }

/*
    @Override
    public void transfer(String data, OnTransferDoneListener listener) {
        InteractionApplication applicationContext = (InteractionApplication) mApplicationContext;
        Client client = new Client(applicationContext.getP2pinfo().groupOwnerAddress, 8888);
        client.registerOnPostExecuteListener(listener);
        client.execute(data);
    }
*/
    @Override
    public void transfer(Payload payload) {

    }

    @Override
    public void register(ConnectionListener listener) {

    }

    @Override
    public void connect(String address) {

    }
}
