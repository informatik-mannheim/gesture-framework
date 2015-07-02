package hs_mannheim.pattern_interaction_model.gesture.shake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import hs_mannheim.pattern_interaction_model.model.GestureDetector;

public class ShakeDetector extends GestureDetector implements SensorEventListener {

    private static final int SHAKE_THRESHOLD = 800;
    private static final int SHAKE_WINDOW = 1500; //TODO: Don't wait forever to accumulate values

    private final Sensor accel;
    private final SensorManager mSensorManager;
    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;

    public ShakeDetector(SensorManager sensorMgr) {
        this.mSensorManager = sensorMgr;
        accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    last_x = last_y = last_z = 0f;
                    lastUpdate = curTime + 2000; /* wait 2 seconds till next shake */
                    fireGestureDetected();
                    Log.d("sensor", "shake detected w/ speed: " + speed);
                    return;

                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

