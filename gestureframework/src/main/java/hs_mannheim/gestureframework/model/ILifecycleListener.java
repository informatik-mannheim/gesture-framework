package hs_mannheim.gestureframework.model;

/**
 * Listens for {@link LifecycleEvent} occurrences.
 */
public interface ILifecycleListener {
    void onConnect();
    void onSelect();
    void onTransfer();
    void onDisconnect();
}
