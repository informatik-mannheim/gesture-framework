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

package hs_mannheim.gestureframework.gesture;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;

import hs_mannheim.gestureframework.messaging.PostOffice;
import hs_mannheim.gestureframework.gesture.bump.BumpDetector;
import hs_mannheim.gestureframework.gesture.bump.Threshold;
import hs_mannheim.gestureframework.gesture.doubletap.DoubleTapDetector;
import hs_mannheim.gestureframework.gesture.shake.ShakeDetector;
import hs_mannheim.gestureframework.gesture.stitch.StitchDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDirectionConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDurationConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.SwipeMinDistanceConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeOrientationConstraint;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.model.IViewContext;

/**
 * Helps with the setup of example {@link GestureDetector} instances. This is just for testing and
 * convenience and not really basic framework functionality.
 */
public class GestureDetectorBuilder {
    PostOffice mPostOffice;
    IViewContext mViewContext;
    Context mContext;

    public GestureDetectorBuilder(PostOffice postOffice,
                                  IViewContext viewContext,
                                  Context context) {
        this.mPostOffice = postOffice;
        this.mViewContext = viewContext;
        this.mContext = context;
    }

    //TODO: Add constraints as parameters
    public StitchDetector createStitchDetector() {
        StitchDetector stitchDetector = new StitchDetector(mPostOffice, mViewContext);
        stitchDetector.addConstraint(new SwipeOrientationConstraint(SwipeEvent.Orientation.WEST));
        stitchDetector.addConstraint(new SwipeDurationConstraint(1000));
        return stitchDetector;

    }

    //TODO: Add constraints as parameters
    public SwipeDetector createSwipeLeftRightDetector() {
        return new SwipeDetector(mViewContext)
                .addConstraint(new SwipeDirectionConstraint(SwipeEvent.Direction.HORIZONTAL))
                .addConstraint(new SwipeDurationConstraint(1000))
                .addConstraint(new SwipeMinDistanceConstraint(200))
                .addSwipeListener(new GestureDetectorBuilder.DebugSwipeListener());
    }

    //TODO: BUMPED UP SWIPEMINDISTANCECONSTRAINT FROM 200 TO 500 IN ORDER TO TEST ANIMATIONS!
    public SwipeDetector createSwipeUpDownDetector() {
        return new SwipeDetector(mViewContext)
                .addConstraint(new SwipeDirectionConstraint(SwipeEvent.Direction.VERTICAL))
                .addConstraint(new SwipeDurationConstraint(1000))
                .addConstraint(new SwipeMinDistanceConstraint(500))
                .addSwipeListener(new GestureDetectorBuilder.DebugSwipeListener());
    }

    public ShakeDetector createShakeDetector() {
        SensorManager sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        return new ShakeDetector(sm, mViewContext);
    }

    //TODO: Add threshold as param?
    public BumpDetector createBumpDetector() {
        SensorManager sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        return new BumpDetector(sm, Threshold.HORST, mViewContext);
    }

    public DoubleTapDetector createDoubleTapDetector() {
        return new DoubleTapDetector(mViewContext);
    }

    public class DebugSwipeListener implements SwipeDetector.SwipeEventListener {

        @Override
        public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event) {
            Log.d("[SwipeDetector]", "Swipe Detected");
        }

        @Override
        public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {

        }

        @Override
        public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {

        }

        @Override
        public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {

        }
    }
}
