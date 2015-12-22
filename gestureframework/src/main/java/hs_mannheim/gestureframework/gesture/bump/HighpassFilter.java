package hs_mannheim.gestureframework.gesture.bump;

public class HighpassFilter {
    private final double K_FILTERING_FACTOR = 0.1;

    public void applyTo(Sample currentData, Sample oldData) {
        // Subtract the low-pass value from the current value to get a simplified high-pass filter
        oldData.x = currentData.x - ((currentData.x * K_FILTERING_FACTOR) + (oldData.x * (1.0 - K_FILTERING_FACTOR)));
        oldData.y = currentData.y - ((currentData.y * K_FILTERING_FACTOR) + (oldData.y * (1.0 - K_FILTERING_FACTOR)));
        oldData.z = currentData.z - ((currentData.z * K_FILTERING_FACTOR) + (oldData.z * (1.0 - K_FILTERING_FACTOR)));
        oldData.timestamp = currentData.timestamp;
    }
}