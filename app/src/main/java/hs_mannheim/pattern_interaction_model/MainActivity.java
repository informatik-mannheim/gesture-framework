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
import hs_mannheim.pattern_interaction_model.model.IPacketReceiver;
import hs_mannheim.pattern_interaction_model.model.InteractionContext;
import hs_mannheim.pattern_interaction_model.model.Packet;
import hs_mannheim.pattern_interaction_model.model.PacketType;


public class MainActivity extends ActionBarActivity {

    public final static String MODEL = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.tvHeaderMain)).setText(MODEL);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //InteractionContext interactionContext = new Configuration().wifiBump((InteractionApplication) getApplicationContext());
        //InteractionContext interactionContext = new Configuration().bluetoothSwipe((InteractionApplication) getApplicationContext(), findViewById(R.id.layout_main), this);
        //InteractionContext interactionContext = new Configuration().wifiShake((InteractionApplication) getApplicationContext());
        //((InteractionApplication) getApplicationContext()).setInteractionContext(interactionContext);
    }

    public void buildClicked(View view) {
        InteractionContext interactionContext = new Configuration().wifiShake((InteractionApplication) getApplicationContext());
        ((InteractionApplication) getApplicationContext()).setInteractionContext(interactionContext);

        startActivity(new Intent(this, InteractionActivity.class));
    }
}
