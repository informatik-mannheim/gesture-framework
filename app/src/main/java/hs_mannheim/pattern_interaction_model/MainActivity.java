package hs_mannheim.pattern_interaction_model;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.Selection;


public class MainActivity extends ActionBarActivity implements IViewContext {

    public final static String MODEL = Build.MODEL;
    public Point DISPLAY_SIZE = new Point();
    private RadioGroup mConnectionGroup;
    private RadioGroup mGestureGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindowManager().getDefaultDisplay().getRealSize(DISPLAY_SIZE);

        ((TextView) findViewById(R.id.tvHeaderMain)).setText(MODEL);

        mConnectionGroup = (RadioGroup) findViewById(R.id.rgConnection);
        mGestureGroup = (RadioGroup) findViewById(R.id.rgGesture);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void buildClicked(View view) {
        int gestureId = mGestureGroup.getCheckedRadioButtonId();
        int connectionId = mConnectionGroup.getCheckedRadioButtonId();

        ConfigurationBuilder builder = new ConfigurationBuilder(getApplicationContext(), this);

        if (connectionId == R.id.rbBluetooth) {
            builder.withBluetooth();
        }
        else {
            builder.withWifiDirect();
        }

        switch (gestureId) {
            case R.id.rbBump:
                builder.bump();
                break;
            case R.id.rbShake:
                builder.shake();
                break;
            case R.id.rbStitch:
                builder.stitch();
                break;
            case R.id.rbSwipe:
                builder.swipe();
                break;
        }

        builder.select(new Selection(new Packet("Empty"))).buildAndRegister();

        startActivity(new Intent(this, InteractionActivity.class));
    }

    @Override
    public View getInteractionView() {
        return findViewById(R.id.layout_main);
    }

    @Override
    public Point getDisplaySize() {
        return DISPLAY_SIZE;
    }
}
