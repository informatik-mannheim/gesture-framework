package hs_mannheim.gestureframework.model;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;

import hs_mannheim.gestureframework.connection.PostOffice;
import hs_mannheim.gestureframework.gesture.bump.BumpDetector;
import hs_mannheim.gestureframework.gesture.bump.Threshold;
import hs_mannheim.gestureframework.gesture.shake.ShakeDetector;
import hs_mannheim.gestureframework.gesture.stitch.StitchDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDirectionConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDurationConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.SwipeMinDistanceConstraint;
import hs_mannheim.gestureframework.gesture.swipe.SwipeOrientationConstraint;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

/**
 * Created by Dominick Madden on 21.04.2016.
 */
public class GestureDetectorBuilder{

    PostOffice mPostOffice;
    IViewContext mViewContext;
    Context mContext;
    GestureDetector mDetector;

    public GestureDetectorBuilder(PostOffice postOffice, IViewContext viewContext, Context context){
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
    public SwipeDetector createSwipeDetector() {
        return new SwipeDetector(mViewContext)
                .addConstraint(new SwipeDirectionConstraint(SwipeEvent.Direction.VERTICAL))
                .addConstraint(new SwipeDurationConstraint(1000))
                .addConstraint(new SwipeMinDistanceConstraint(700))
                .addConstraint(new SwipeOrientationConstraint(SwipeEvent.Orientation.NORTH))
                .addSwipeListener(new GestureDetectorBuilder.DebugSwipeListener());
    }

    public ShakeDetector createShakeDetector() {
        return new ShakeDetector((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE), mViewContext);
    }

    //TODO: Add threshold as param?
    public BumpDetector createBumpDetector() {
        return new BumpDetector((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE), Threshold.LOW, mViewContext);
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
