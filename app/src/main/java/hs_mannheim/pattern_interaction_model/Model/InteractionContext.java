package hs_mannheim.pattern_interaction_model.Model;

import android.util.Log;

public class InteractionContext implements GestureDetector.GestureEventListener, OnTransferDoneListener, ConnectionListener {
    private final GestureDetector mGestureDetector;
    private final Selection mSelection;
    private final IConnection mConnection;

    public InteractionContext(GestureDetector gestureDetector,
                              Selection selection,
                              IConnection connection) {

        mGestureDetector = gestureDetector;
        mSelection = selection;
        mConnection = connection;

        // as a default, let this be the client for all listening.
        mConnection.register(this);
        mGestureDetector.registerGestureEventListener(this);

    }

    public IConnection getConnection() {
        return this.mConnection;
    }

    public void registerConnectionListener(ConnectionListener listener) {
        this.mConnection.register(listener);
    }

    @Override
    public void onGestureDetected() {
        mConnection.transfer(mSelection.getData(), this);
    }

    @Override
    public void onTransferSuccess() {
        Log.d("Interaction Context", "transfer success");
    }

    @Override
    public void onTransferFailure() {
        Log.d("Interaction Context", "transfer failure");
    }

    @Override
    public void onConnectionEstablished() {

    }

    @Override
    public void onDataReceived(String data) {

    }

    @Override
    public void onConnectionLost() {

    }
}
