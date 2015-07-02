package hs_mannheim.pattern_interaction_model;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.modeltemp.IPacketReceiver;
import hs_mannheim.pattern_interaction_model.modeltemp.InteractionContext;
import hs_mannheim.pattern_interaction_model.modeltemp.Packet;


public class MainActivity extends ActionBarActivity implements SwipeDetector.SwipeEventListener, IPacketReceiver, TextWatcher {

    public final static String MODEL = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView header = (TextView) findViewById(R.id.tvHeaderMain);
        ((EditText) findViewById(R.id.etMessage)).addTextChangedListener(this);

        header.setText(MODEL);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //InteractionContext interactionContext = new Configuration().wifiBump((InteractionApplication) getApplicationContext());
        //InteractionContext interactionContext = new Configuration().bluetoothSwipe((InteractionApplication) getApplicationContext(), findViewById(R.id.layout_main), this);
        InteractionContext interactionContext = new Configuration().wifiShake((InteractionApplication) getApplicationContext());
        ((InteractionApplication) getApplicationContext()).setInteractionContext(interactionContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().unregister(this);
    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {
        Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show();
    }

    public void startBluetoothActivity(View view) {
        startActivity(new Intent(this, BluetoothActivity.class));
    }

    public void startWifiDirectActivity(View view) {
        startActivity(new Intent(this, WifiDirectActivity.class));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        ((InteractionApplication) getApplicationContext()).getInteractionContext().updateSelection(new Packet("DATA", s.toString()));
    }

    public void startStitchView(View view) {
        startActivity(new Intent(this, StitchView.class));
    }

    @Override
    public void receive(Packet packet) {
        Toast.makeText(this, packet.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean accept(String type) {
        return true;
    }
}
