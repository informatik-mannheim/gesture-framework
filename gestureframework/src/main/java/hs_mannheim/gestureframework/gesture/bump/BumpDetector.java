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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.model.IViewContext;

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

    public BumpDetector(SensorManager sensorManager, Threshold threshold, IViewContext viewContext) {
        super(viewContext);
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

        for (Sample s : mSamples) {
            Log.d(TAG, s.toString());
        }


        return peaks.between(minPeaks, maxPeaks);
    }

    private void fireBumpEvent() {
        this.fireGestureDetected();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
