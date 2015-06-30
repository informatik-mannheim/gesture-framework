package hs_mannheim.pattern_interaction_model.model;

public class InteractionContext implements GestureDetector.GestureEventListener, ConnectionListener {
    private final GestureDetector _gestureDetector;
    private final Selection _selection;
    private final IConnection _connection;

    public InteractionContext(GestureDetector gestureDetector,
                              Selection selection,
                              IConnection connection) {

        _gestureDetector = gestureDetector;
        _selection = selection;
        _connection = connection;

        // as a default, let this be the client for all listening.
        _connection.register(this);
        _gestureDetector.registerGestureEventListener(this);
    }

    public IConnection getConnection() {
        return this._connection;
    }

    public void registerConnectionListener(ConnectionListener listener) {
        this._connection.register(listener);
    }

    public void updateSelection(Payload data) {
        _selection.updateSelection(data);
    }

    @Override
    public void onGestureDetected() {
        _connection.transfer(_selection.getData());
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
