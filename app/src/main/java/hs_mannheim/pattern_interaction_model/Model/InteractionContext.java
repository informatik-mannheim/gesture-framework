package hs_mannheim.pattern_interaction_model.model;

import hs_mannheim.pattern_interaction_model.PostOffice;

public class InteractionContext implements GestureDetector.GestureEventListener {

    private final GestureDetector mGestureDetector;
    private final Selection mSelection;
    private final IConnection mConnection;
    private final IPostOffice mPostOffice;

    public InteractionContext(GestureDetector gestureDetector,
                              Selection selection,
                              IConnection connection) {

        mGestureDetector = gestureDetector;
        mSelection = selection;
        mConnection = connection;
        mPostOffice = new PostOffice(mConnection); /* only PostOffice talks to the connection */
        mGestureDetector.registerGestureEventListener(this);
    }

    public IPostOffice getPostOffice() {
        return mPostOffice;
    }

    public IConnection getConnection() {
        return this.mConnection;
    }

    public void updateSelection(Packet data) {
        mSelection.updateSelection(data);
    }

    @Override
    public void onGestureDetected() {
        mPostOffice.send(mSelection.getData());
    }
}
