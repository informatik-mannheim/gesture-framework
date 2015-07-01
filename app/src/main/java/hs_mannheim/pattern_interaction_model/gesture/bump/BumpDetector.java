package hs_mannheim.pattern_interaction_model.gesture.bump;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

import hs_mannheim.pattern_interaction_model.model.GestureDetector;

public class BumpDetector extends GestureDetector implements SensorEventListener {

    private final static Object mLockObj = new Object();
    private final int M_SAMPLING_RATE = 100;

    private final String TAG = "[BumpDetector]";

    private final Sensor mAccelerometer;
    private SensorManager mSensorManager;

    private boolean mRecording = false;

    private Sample mCurrentSample = new Sample(0.0, 0.0, 0.0, 0);
    private Sample mPreviousSample = new Sample(0.0, 0.0, 0.0, 0);

    private ArrayList<Sample> mSamples = new ArrayList<>();

    private Threshold mThreshold;

    public BumpDetector(SensorManager sensorManager, Threshold threshold) {
        this.mSensorManager = sensorManager;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mThreshold = threshold;
        System.out.println(mSamples.size());
        this.startMonitoring();
    }

    public void setThreshold(Threshold threshold) {
        this.mThreshold = threshold;
    }

    public void startMonitoring() {
        Log.d(TAG, "start monitoring");
        mSamples.clear();
        mSensorManager.registerListener(this, mAccelerometer, M_SAMPLING_RATE);
    }

    public void stopMonitoring() {
        Log.d(TAG, "stop monitoring");
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (mLockObj) {
            Sample newData = new Sample(event.values, event.timestamp);
            new HighpassFilter().applyTo(newData, mCurrentSample);
            Delta delta = mCurrentSample.delta(mPreviousSample);
            if (delta.exceedsThreshold(mThreshold) && !mRecording) {
                mSamples.add(mCurrentSample.clone());
                mRecording = true;
                Log.d(TAG, "start recording");
            } else if (mRecording) {
                if (mSamples.size() < 31) {
                    mSamples.add(mCurrentSample.clone());
                } else {
                    stopMonitoring();
                    mRecording = false;
                    checkForBump();
                }
            }
        }
        mPreviousSample = mCurrentSample.clone();
    }

    private void checkForBump() {
        if (isBump()) {
            Log.d(TAG, "Bump detected!");
            fireBumpEvent();
        } else {
            Log.d(TAG, "False alarm.");
        }

        startMonitoring();
    }

    private boolean isBump() {
        Peaks peaks = Peaks.readFrom(mSamples);
        int minPeaks = mSamples.size() - 10;
        int maxPeaks = mSamples.size() - 2;

        return peaks.between(minPeaks, maxPeaks);
    }

    private void fireBumpEvent() {
        this.onGestureDetected();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
