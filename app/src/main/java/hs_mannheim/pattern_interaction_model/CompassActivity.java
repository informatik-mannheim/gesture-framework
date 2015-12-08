package hs_mannheim.pattern_interaction_model;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends ActionBarActivity implements SensorEventListener {

    private ImageView mPointer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private TextView mDegrees;
    private float accumulator;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        mPointer = (ImageView) findViewById(R.id.pointer);
        mDegrees = (TextView) findViewById(R.id.tvDegrees);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        counter = 0;
        accumulator = 0;
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(1000);
            ra.setFillAfter(true);

            if (counter < 10) {
                counter++;
                accumulator += azimuthInDegress;
            } else {
                counter = 0;
                float meanDegrees = accumulator / 10;
                String degrees = Float.toString(meanDegrees);

                if (meanDegrees <= 10) {
                    degrees += " (N)";
                } else if (meanDegrees > 10 && meanDegrees <= 80) {
                    degrees += " (NE)";
                } else if (meanDegrees > 80 && meanDegrees <= 100) {
                    degrees += " (E)";
                } else if (meanDegrees > 100 && meanDegrees <= 170) {
                    degrees += " (SE)";
                } else if (meanDegrees > 170 && meanDegrees <= 190) {
                    degrees += " (S)";
                } else if (meanDegrees > 190 && meanDegrees <= 260) {
                    degrees += " (SW)";
                } else if (meanDegrees > 260 && meanDegrees <= 280) {
                    degrees += " (W)";
                } else if (meanDegrees > 280 && meanDegrees <= 360) {
                    degrees += " (NW)";
                }

                mDegrees.setText(degrees);
                accumulator = 0;
            }


            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
}
