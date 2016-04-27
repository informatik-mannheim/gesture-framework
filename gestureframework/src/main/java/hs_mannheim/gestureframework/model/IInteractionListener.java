package hs_mannheim.gestureframework.model;

public interface IInteractionListener {
    void onConnect();
    void onSelect();
    void onTransfer();
    void onDisconnect();
}
