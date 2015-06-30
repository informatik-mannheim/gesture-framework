package hs_mannheim.pattern_interaction_model.model;

public class InteractionContext implements GestureDetector.GestureEventListener, ConnectionListener {
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
        mConnection.transfer(new Payload("DATA", mSelection.getData()));
    }

    @Override
    public void onConnectionEstablished() {

    }

    @Override
    public void onDataReceived(Payload data) {

    }

    @Override
    public void onConnectionLost() {

    }
}
