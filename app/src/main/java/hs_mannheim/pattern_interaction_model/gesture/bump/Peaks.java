package hs_mannheim.pattern_interaction_model.gesture.bump;


import java.util.ArrayList;

public class Peaks {
    public int x;
    public int y;
    public int z;

    public Peaks(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Peaks readFrom(ArrayList<Sample> samples) {
        Peaks peaks = new Peaks(0, 0, 0);

        for (int i = 1; i < samples.size() - 1; i++) {
            Sample value = samples.get(i);
            Sample before = samples.get(i - 1);
            Sample next = samples.get(i + 1);

            if (isPeak(value.x, before.x, next.x)) {
                peaks.x++;
            }
            if (isPeak(value.y, before.y, next.y)) {
                peaks.y++;
            }
            if (isPeak(value.z, before.z, next.z)) {
                peaks.z++;
            }
        }

        return peaks;
    }

    private static boolean isPeak(double value, double before, double next) {
        return value < before && value < next || value > before && value > next;
    }

    public boolean between(int minPeaks, int maxPeaks) {
        return x >= minPeaks && x <= maxPeaks &&
                y >= minPeaks && y <= maxPeaks &&
                z >= minPeaks && z <= maxPeaks;
    }
}