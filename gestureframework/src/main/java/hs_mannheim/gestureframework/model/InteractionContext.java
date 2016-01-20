package hs_mannheim.gestureframework.model;

import android.database.Observable;

public class InteractionContext extends Observable<AllEventsListener> implements GestureDetector.GestureEventListener, IPacketReceiver {

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

    public GestureDetector getGestureDetector(){
        return this.mGestureDetector;
    }

    @Override
    public void onGestureDetected() {
        notifyTransferStarted();
        mPostOffice.send(mSelection.getData());
    }

    private void notifyTransferStarted() {
        for (AllEventsListener listener : mObservers) {
            listener.onTransferStarted();
        }
    }

    @Override
    public void receive(Packet packet) {
        //todo: register to postoffice and distribute stuff
    }

    @Override
    public boolean accept(PacketType type) {
        return false;
    }
}
