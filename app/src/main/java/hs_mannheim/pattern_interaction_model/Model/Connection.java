package hs_mannheim.pattern_interaction_model.Model;

import android.content.Context;

import hs_mannheim.pattern_interaction_model.Client;
import hs_mannheim.pattern_interaction_model.InteractionApplication;

public class Connection implements IConnection {

    private final Context mApplicationContext;

    public Connection(Context applicationContext) {
        this.mApplicationContext = applicationContext;
    }

    @Override
    public boolean isConnected() {
        return true; // for now
    }


    @Override
    public void transfer(String data, OnTransferDoneListener listener) {
        InteractionApplication applicationContext = (InteractionApplication) mApplicationContext;
        Client client = new Client(applicationContext.getP2pinfo().groupOwnerAddress, 8888);
        client.registerOnPostExecuteListener(listener);
        client.execute(data);
    }
}
