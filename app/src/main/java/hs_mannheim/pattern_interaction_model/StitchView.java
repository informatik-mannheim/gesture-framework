package hs_mannheim.pattern_interaction_model;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.pattern_interaction_model.gesture.stitch.StitchDetector;
import hs_mannheim.pattern_interaction_model.model.GestureDetector;
import hs_mannheim.pattern_interaction_model.model.IPacketReceiver;
import hs_mannheim.pattern_interaction_model.model.IPostOffice;
import hs_mannheim.pattern_interaction_model.model.Packet;
import hs_mannheim.pattern_interaction_model.model.PacketType;
import hs_mannheim.pattern_interaction_model.model.StitchPacket;


public class StitchView extends ActionBarActivity implements GestureDetector.GestureEventListener, IPacketReceiver {

    private StitchDetector mStitchDetector;
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

        ((TextView) findViewById(R.id.tvHeader)).setText(String.format("x: %d; y: %d", screenX, screenY));

        InteractionApplication applicationContext = (InteractionApplication) getApplicationContext();

        IPostOffice postOffice = applicationContext.getInteractionContext().getPostOffice();

        postOffice.register(this);

        mStitchDetector = new StitchDetector(postOffice);
        mStitchDetector.registerGestureEventListener(this);
        mStitchDetector.attachToView(findViewById(R.id.stitchView));
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
        Toast.makeText(this, "Stitch detected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void receive(Packet packet) {
        StitchPacket stitchPacket = (StitchPacket) packet;
        Toast.makeText(this, stitchPacket.toString() + stitchPacket.getSwipeEvent().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean accept(PacketType type) {
        return type.equals(PacketType.Stitch);
    }
}
