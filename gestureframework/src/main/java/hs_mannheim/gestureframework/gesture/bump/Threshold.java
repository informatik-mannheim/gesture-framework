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

/**
 * Provides predefined thresholds for bump recognition.
 */
@SuppressWarnings("unused")
public enum Threshold {
    ZERO(0, 0, 0),
    LOW(7.5, 7.5, 4.5),
    HORST(9, 9 , 5.5), // works best on Horst's Nexus 5 Phone.
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
