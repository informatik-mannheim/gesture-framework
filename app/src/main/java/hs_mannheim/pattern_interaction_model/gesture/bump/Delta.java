package hs_mannheim.pattern_interaction_model.gesture.bump;

public class Delta extends Sample {
    public Delta(double x, double y, double z) {
        super(x, y, z);
    }

    public boolean exceedsThreshold(Threshold threshold) {
        return this.x > threshold.x && this.y > threshold.y && this.z > threshold.z;
    }
}