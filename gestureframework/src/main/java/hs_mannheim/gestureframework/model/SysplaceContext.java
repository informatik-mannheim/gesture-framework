package hs_mannheim.gestureframework.model;

public class SysplaceContext implements IPacketReceiver, ILifecycleListener, ISysplaceContext {

    private final GestureManager mGestureManager;
    private final Selection mSelection;
    private final IConnection mConnection;
    private final IPostOffice mPostOffice;

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
        mSelection = selection;
        mConnection = connection;
        mPostOffice = postOffice; /* only PostOffice talks to the connection */

        mGestureManager.registerLifecycleListener(this);
        mPostOffice.register(mGestureManager);
    }

    public IConnection getConnection() {
        return this.mConnection;
    }

    public void updateSelection(Packet data) {
        mSelection.updateSelection(data);
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
    public boolean accept(PacketType type) {
        return false;
    }

    public GestureManager getGestureManager() {
        return mGestureManager;
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onTransfer() {
        mPostOffice.send(mSelection.getData());
    }

    @Override
    public void onDisconnect() {

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
}
