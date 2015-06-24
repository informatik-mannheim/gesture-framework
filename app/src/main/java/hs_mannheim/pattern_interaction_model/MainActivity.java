package hs_mannheim.pattern_interaction_model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import hs_mannheim.pattern_interaction_model.Gestures.SwipeDetector;
import hs_mannheim.pattern_interaction_model.Gestures.SwipeDirectionConstraint;
import hs_mannheim.pattern_interaction_model.Gestures.SwipeDurationConstraint;
import hs_mannheim.pattern_interaction_model.Gestures.SwipeOrientationConstraint;


public class MainActivity extends ActionBarActivity implements SwipeDetector.SwipeEventListener {

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Server.ACTION_DATA_RECEIVED);

        this.mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, intent.getStringExtra("data"), Toast.LENGTH_SHORT).show();
            }
        };

        registerSwipeListener();
    }

    private void registerSwipeListener() {
        new SwipeDetector()
                .addConstraint(new SwipeDirectionConstraint(SwipeDetector.Direction.HORIZONTAL))
                .addConstraint(new SwipeDurationConstraint(250))
                .addConstraint(new SwipeOrientationConstraint(SwipeDetector.Orientation.EAST))
                .attachToView(findViewById(R.id.layout_main), this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

    }

    public void startWifiDirectActivity(View view) {
        Intent intent = new Intent(this, WifiDirectActivity.class);
        startActivity(intent);
    }

    public void send(View view) {
        InteractionApplication applicationContext = (InteractionApplication) getApplicationContext();
        new Client(applicationContext.getP2pinfo().groupOwnerAddress, 8888).execute();
    }

    @Override
    public void onSwipeDetected(SwipeDetector.SwipeEvent event) {
        Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show();
        send(null);
    }
}
