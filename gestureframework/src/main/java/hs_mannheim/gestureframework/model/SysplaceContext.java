package hs_mannheim.gestureframework.model;

import android.content.Intent;

import hs_mannheim.gestureframework.connection.BluetoothPairingService;
import hs_mannheim.gestureframework.connection.IConnection;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.IPostOffice;
import hs_mannheim.gestureframework.messaging.Packet;

public class SysplaceContext implements IPacketReceiver, ILifecycleListener, ISysplaceContext {

    private final GestureManager mGestureManager;
    private final IConnection mConnection;
    private final IPostOffice mPostOffice;
    private Selection mSelection;
    private InteractionApplication mApplication;

    /**
     * The InteractionContext is the one and only global object to manage all Sysplace related
     * Gestures, corresponding events, states etc.
     *
     * @param gestureManager The GestureManager for registerign GestureDetectors to LifecycleEvents
     * @param selection The selected data to be transferred
     * @param connection The underlying connection for Peer-To-Peer communication
     * @param postOffice The broker for messages between devices
     */
    public SysplaceContext(GestureManager gestureManager, Selection selection, IConnection connection, IPostOffice postOffice) {
        mGestureManager = gestureManager;
        mConnection = connection;
        mPostOffice = postOffice; /* only PostOffice talks to the connection */

        mGestureManager.registerLifecycleListener(this);
        mPostOffice.register(mGestureManager);

        select(selection);
    }

    public IConnection getConnection() {
        return this.mConnection;
    }

    public void updateViewContextAll(IViewContext viewContext) {
        mGestureManager.setViewContextAll(viewContext);
    }

    public void updateViewContext(LifecycleEvent lifecycleEvent, IViewContext viewContext) {
        mGestureManager.setViewContext(lifecycleEvent, viewContext);
    }

    @Override
    public void registerPacketReceiver(IPacketReceiver packetReceiver) {
        mPostOffice.register(packetReceiver);
    }

    @Override
    public void unregisterPacketReceiver(IPacketReceiver packetReceiver) {
        mPostOffice.unregister(packetReceiver);
    }

    @Override
    public void receive(Packet packet) {
        //todo: register to postoffice and distribute stuff
    }

    @Override
    public boolean accept(Packet.PacketType type) {
        return false;
    }

    public GestureManager getGestureManager() {
        return mGestureManager;
    }

    @Override
    public void onConnect() {
        mApplication.startService(new Intent(mApplication, BluetoothPairingService.class));
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onTransfer() {
        if(mSelection != Selection.Empty) {
            mPostOffice.send(mSelection.getData());
        }
    }

    @Override
    public void onDisconnect() {
        mConnection.disconnect();
    }

    @Override
    public void select(Selection selection) {
        mSelection = selection;
    }

    /**
     * Send packet through an established connection, if any.
     *
     * @param packet The packet to send
     */
    @Override
    public void send(Packet packet) {
        mPostOffice.send(packet);
    }


    /**
     * Register a LifecycleListener to be notified of all LifecycleEvents happening in the
     * {@link SysplaceContext}.
     */
    @Override
    public void registerForLifecycleEvents(ILifecycleListener listener) {
        mGestureManager.registerLifecycleListener(listener);
    }

    public void setApplication(InteractionApplication application) {
        mApplication = application;
    }

    public void applicationPaused() {
        mApplication.toggleName(false);
    }

    public void applicationResumed() {
        mApplication.toggleName(true);
    }
}
