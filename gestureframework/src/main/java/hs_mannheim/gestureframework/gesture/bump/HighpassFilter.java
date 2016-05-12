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