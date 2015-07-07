package hs_mannheim.pattern_interaction_model;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeDetector;
import hs_mannheim.pattern_interaction_model.gesture.swipe.SwipeEvent;
import hs_mannheim.pattern_interaction_model.model.IPacketReceiver;
import hs_mannheim.pattern_interaction_model.model.IViewContext;
import hs_mannheim.pattern_interaction_model.model.InteractionContext;
import hs_mannheim.pattern_interaction_model.model.Packet;
import hs_mannheim.pattern_interaction_model.model.PacketType;


public class InteractionActivity extends ActionBarActivity implements SwipeDetector.SwipeEventListener, IPacketReceiver, TextWatcher, IViewContext {

    public final static String MODEL = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);

        TextView header = (TextView) findViewById(R.id.tv_header_interaction);
        ((EditText) findViewById(R.id.etMessage)).addTextChangedListener(this);

        header.setText(MODEL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InteractionContext interactionContext = ((InteractionApplication) getApplicationContext()).getInteractionContext();
        interactionContext.getPostOffice().register(this);
        interactionContext.updateViewContext(this);
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
        ((InteractionApplication) getApplicationContext()).getInteractionContext().updateSelection(new Packet(s.toString()));
    }

    @Override
    public void receive(Packet packet) {
        Toast.makeText(this, packet.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean accept(PacketType type) {
        return type.equals(PacketType.PlainStringPacket);
    }

    @Override
    public View getInteractionView() {
        return findViewById(R.id.layout_interaction);
    }

    @Override
    public Point getDisplaySize() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(displaySize);
        return displaySize;
    }
}