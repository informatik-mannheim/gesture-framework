package hs_mannheim.gestureframework.model;

public interface ILifecycleListener {
    void onConnect();
    void onSelect();
    void onTransfer();
    void onDisconnect();
}
