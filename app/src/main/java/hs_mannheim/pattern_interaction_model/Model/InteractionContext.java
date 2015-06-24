package hs_mannheim.pattern_interaction_model.Model;


public class InteractionContext implements GestureDetector.GestureEventListener {
    private final GestureDetector mGestureDetector;
    private final Selection mSelection;
    private final IConnection mConnection;

    public InteractionContext(GestureDetector gestureDetector,
                              Selection selection,
                              IConnection connection) {

        mGestureDetector = gestureDetector;
        mSelection = selection;
        mConnection = connection;
        mGestureDetector.registerGestureEventListener(this);
    }

    @Override
    public void onGestureDetected() {
        mConnection.transfer(mSelection.getData());
    }
}
