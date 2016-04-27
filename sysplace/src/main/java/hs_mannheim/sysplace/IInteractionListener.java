package hs_mannheim.sysplace;

public interface IInteractionListener {
    void onConnect();
    void onSelect();
    void onTransfer();
    void onDisconnect();

}
