package hs_mannheim.pattern_interaction_model.model;

public class InteractionContext implements GestureDetector.GestureEventListener {

    private final GestureDetector mGestureDetector;
    private final Selection mSelection;
    private final IConnection mConnection;
    private final IPostOffice mPostOffice;

    public InteractionContext(GestureDetector gestureDetector,
                              Selection selection,
                              IConnection connection,
                              IPostOffice postOffice) {

        mGestureDetector = gestureDetector;
        mSelection = selection;
        mConnection = connection;
        mPostOffice = postOffice; /* only PostOffice talks to the connection */
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

    public void updateViewContext(IViewContext viewContext) {
        mGestureDetector.setViewContext(viewContext);
    }

    @Override
    public void onGestureDetected() {
        mPostOffice.send(mSelection.getData());
    }
}
