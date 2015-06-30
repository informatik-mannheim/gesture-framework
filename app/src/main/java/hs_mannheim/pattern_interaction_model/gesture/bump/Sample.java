package hs_mannheim.pattern_interaction_model.gesture.bump;

public class Sample implements Cloneable {
    public double x;
    public double y;
    public double z;
    public long timestamp;

    public Sample() {

    }

    public Sample(float[] values, long timestamp) throws IllegalArgumentException {
        if(values.length != 3) {
            throw new IllegalArgumentException("values must have a length of 3");
        }
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
        this.timestamp = timestamp;

    }

    public Sample(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Sample(double x, double y, double z, long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
    }

    public Delta delta(Sample other) {
        return new Delta(delta(this.x, other.x), delta(this.y, other.y), delta(this.z, other.z));
    }

    private double delta(double left, double right) {
        if (left < 0 || right < 0) {
            return Math.abs(left) + Math.abs(right);
        } else {
            return Math.abs(Math.abs(left) - Math.abs(right));
        }
    }

    @Override
    public Sample clone() {
        return new Sample(this.x, this.y, this.z, this.timestamp);
    }

    @Override
    public boolean equals(Object other){
        if ( this == other ) return true;
        if ( !(other instanceof Sample) ) return false;
        Sample otherSample = (Sample)other;
        return
                this.x == otherSample.x && this.y == otherSample.y && this.z == otherSample.z && this.timestamp == otherSample.timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + Double.valueOf(this.x).hashCode();
        hash = 31 * hash + Double.valueOf(this.y).hashCode();
        hash = 31 * hash + Double.valueOf(this.z).hashCode();
        hash = 31 * hash + Long.valueOf(timestamp).hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Sample(x: " + this.x + ", y: " + this.y + ", z: " + this.z + ", timestamp: " + this.timestamp + ")";
    }
}