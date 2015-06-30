package hs_mannheim.pattern_interaction_model.gesture.bump;

public interface IBumpDetector {
    void registerListener(BumpEventListener listener);

    void setThreshold(Threshold threshold);

    void startMonitoring(int delayInMillis);

    void startMonitoring();

    void stopMonitoring();
}
