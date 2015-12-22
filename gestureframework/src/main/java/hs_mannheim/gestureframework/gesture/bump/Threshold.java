package hs_mannheim.gestureframework.gesture.bump;

public enum Threshold {
    ZERO(0, 0, 0),
    LOW(7.5, 7.5, 4.5),
    MEDIUM(23, 23, 18),
    HIGH(40, 40, 27);

    double x;
    double y;
    double z;

    Threshold(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
