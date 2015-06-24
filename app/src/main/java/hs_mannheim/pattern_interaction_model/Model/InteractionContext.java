package hs_mannheim.pattern_interaction_model.Model;


import android.util.Log;

public class InteractionContext implements GestureDetector.GestureEventListener, OnTransferDoneListener {
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
}
