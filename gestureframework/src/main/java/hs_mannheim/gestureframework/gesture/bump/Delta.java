package hs_mannheim.gestureframework.gesture.bump;

public class Delta extends Sample {
    public Delta(double x, double y, double z) {
        super(x, y, z);
    }

    public boolean exceedsThreshold(Threshold threshold) {
        return this.x > threshold.getX() && this.y > threshold.getY() && this.z > threshold.getZ();
    }
}