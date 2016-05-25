/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *     Hochschule Mannheim University of Applied Sciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package hs_mannheim.gestureframework.gesture.approach;

import android.content.Context;
import android.util.Log;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Utils;
import com.estimote.sdk.eddystone.Eddystone;

import java.util.ArrayList;
import java.util.List;

import hs_mannheim.gestureframework.gesture.GestureDetector;
import hs_mannheim.gestureframework.model.IViewContext;

/**
 * Checks for approach gestures by scanning for an Eddystone with a certain Namespace and
 * Instance ID. Will fire an event anytime the distance to the beacon is changing from
 * above-threshold to below-threshold.
 */
@SuppressWarnings("unused")
public class ApproachDetector extends GestureDetector {
    private static final String TAG = "EddystoneScan";
    private static final String EDDYSTONE_NS = "edd1ebeac04e5defa017";
    private static final String EDDYSTONE_INS = "c80743ea119d";
    private static final int NUM_SAMPLES = 5;
    private static final int SCAN_PERIOD_MILLIS = 500;

    private boolean mInRange = false;
    private String mScanId;
    private BeaconManager mBeaconManager;

    private List<Double> mValues = new ArrayList<>();
    private double mThresholdDistance;

    @SuppressWarnings("unused")
    public ApproachDetector(IViewContext viewContext, double thresholdDistance, Context context) {
        super(viewContext);
        mThresholdDistance = thresholdDistance;

        mBeaconManager = new BeaconManager(context);
        mBeaconManager.setForegroundScanPeriod(SCAN_PERIOD_MILLIS, 0);
        mBeaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override
            public void onEddystonesFound(List<Eddystone> list) {
                for (Eddystone e : list) {
                    if (e.instance != null
                            && e.namespace != null
                            && e.instance.equals(EDDYSTONE_INS)
                            && e.namespace.equals(EDDYSTONE_NS)) {
                        handle(e);
                    }
                }
            }
        });

        startScanning();
    }

    /**
     * Calculate distance to Eddystone and calculate mean if enough values have been stored.
     *
     * @param eddystone The Eddystone to which to determine the distance.
     */
    private void handle(Eddystone eddystone) {
        Log.d(TAG, "Found Eddystone "
                + eddystone.instance +
                " at distance "
                + Double.toString(Utils.computeAccuracy(eddystone)));

        mValues.add(Utils.computeAccuracy(eddystone));

        if (mValues.size() == NUM_SAMPLES) {
            Double mean = calculateArithmeticMean(mValues);
            mValues.clear();

            Log.d(TAG, "Mean calculated: " + Double.toString(mean));

            if (mean < mThresholdDistance && !mInRange) {
                Log.d(TAG, "Beacon in range, fire gesture.");
                mInRange = true;
                fireGestureDetected();
            } else if (mean > mThresholdDistance && mInRange){
                Log.d(TAG, "Beacon out of range");
                mInRange = false;
            }
        }
    }

    /**
     * Calculates the arithmetic mean for an array of doubles.
     *
     * @param numbers The numbers to calculate the mean for.
     * @return The arithmetic mean.
     */
    private static Double calculateArithmeticMean(List<Double> numbers) {
        Double sum = 0.0d;

        for (Double d : numbers) {
            sum += d;
        }

        return sum / numbers.size();
    }

    /**
     * Starts scanning for Eddystones matching Namespace and Instance ID.
     */
    @SuppressWarnings("unused")
    public void startScanning() {
        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                mScanId = mBeaconManager.startEddystoneScanning();
            }
        });
    }

    /**
     * Stops scanning for Eddystones.
     */
    @SuppressWarnings("unused")
    public void stopScanning() {
        mBeaconManager.stopEddystoneScanning(mScanId);
    }
}
