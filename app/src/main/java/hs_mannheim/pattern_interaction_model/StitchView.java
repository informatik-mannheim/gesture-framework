package hs_mannheim.pattern_interaction_model;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.model.GestureDetector;


public class StitchView extends ActionBarActivity implements GestureDetector.GestureEventListener, SwipeDetector.SwipeEventListener {

    private SwipeDetector mStitchDetector;
    private int screenX;
    private int screenY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stitch_view);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getRealSize(size);
        screenX = size.x;
        screenY = size.y;

        ((TextView)findViewById(R.id.tvHeader)).setText(String.format("x: %d; y: %d", screenX, screenY));

        mStitchDetector = new SwipeDetector();
        mStitchDetector.registerGestureEventListener(this);
        mStitchDetector.attachToView(findViewById(R.id.stitchView), this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stitch_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGestureDetected() {

    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {
        Toast.makeText(this, "Swipe detected: " + event.toString() + "(" + event.getBounding(screenX, screenY) + ")", Toast.LENGTH_SHORT).show();
    }
}
