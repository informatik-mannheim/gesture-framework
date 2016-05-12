/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework.gesture.bump;


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
        return (x >= minPeaks && x <= maxPeaks) &&
                (y >= minPeaks && y <= maxPeaks) &&
                (z >= minPeaks && z <= maxPeaks);
    }
}