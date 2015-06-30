package hs_mannheim.pattern_interaction_model.gesture.bump;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;


public class BumpDetector implements SensorEventListener, IBumpDetector {

    private final String _label = "BDETECT";
    private final int _samplingRate = 100;
    private static Object _lockobj = new Object();

    private final Sensor _accelerometer;
    private SensorManager _sensorManager;

    private boolean _recording = false;

    private Sample _currentSample = new Sample(0.0, 0.0, 0.0, 0);
    private Sample _previousSample = new Sample(0.0, 0.0, 0.0, 0);

    private ArrayList<Sample> _samples = new ArrayList<>();

    private Threshold mThreshold;
    private BumpEventListener mIBumpListener;

    public BumpDetector(SensorManager sensorManager, Threshold threshold) {
        this._sensorManager = sensorManager;
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mThreshold = threshold;
        System.out.println(_samples.size());
    }

    @Override
    public void registerListener(BumpEventListener listener) {
        mIBumpListener = listener;
    }

    @Override
    public void setThreshold(Threshold threshold) {
        this.mThreshold = threshold;
        Log.d(_label, "Threshold set to: " + threshold.toString());
    }

    @Override
    public void startMonitoring(int delayInMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMonitoring();
            }
        }, delayInMillis);
    }

    @Override
    public void startMonitoring() {
        Log.d(_label, "register");
        _samples.clear();
        _sensorManager.registerListener(this, _accelerometer, _samplingRate);
    }

    @Override
    public void stopMonitoring() {
        Log.d(_label, "unregister");
        _sensorManager.unregisterListener(this, _accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (_lockobj) {
            Sample newData = new Sample(event.values, event.timestamp);
            new HighpassFilter().applyTo(newData, _currentSample);
            Delta delta = _currentSample.delta(_previousSample);
            if (delta.exceedsThreshold(mThreshold) && !_recording) {
                _samples.add(_currentSample.clone());
                _recording = true;
                Log.d(_label, "Started recording.");
            } else if (_recording) {
                if (_samples.size() < 31) {
                    _samples.add(_currentSample.clone());
                } else {
                    stopMonitoring();
                    _recording = false;
                    checkForBump();
                }
            }
        }
        _previousSample = _currentSample.clone();
    }

    private void checkForBump() {
        if (isBump()) {
            Log.d(_label, "Bump detected!");
            fireBumpEvent();
        } else {
            Log.d(_label, "False alarm.");
        }

        startMonitoring();
    }

    private boolean isBump() {
        Log.d(_label, "Checking for Bump...");
        int minPeaks = _samples.size() - 10;
        int maxPeaks = _samples.size() - 2;

        return Peaks.readFrom(_samples).between(minPeaks, maxPeaks);
    }

    private void fireBumpEvent() {
        mIBumpListener.onBump(_samples);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
