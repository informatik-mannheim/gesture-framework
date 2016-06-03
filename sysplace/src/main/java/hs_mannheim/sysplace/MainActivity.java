package hs_mannheim.sysplace;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hs_mannheim.gestureframework.ConfigurationBuilder;
import hs_mannheim.gestureframework.gesture.approach.ApproachDetector;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionApplication;
import hs_mannheim.gestureframework.model.Selection;
import hs_mannheim.gestureframework.model.SysplaceContext;
import hs_mannheim.gestureframework.model.ViewWrapper;

public class MainActivity extends AppCompatActivity implements IViewContext, IPacketReceiver {
    private static final String TAG = "[Main Activity]";

    private TextView mTextView;
    private EditText mEditText;
    private Button mPingButton;
    private Button mPhotoButton;
    private Button mDisconnectButton;
    private SysplaceContext mSysplaceContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "App starts");

        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mTextView = ((TextView) findViewById(R.id.textView));
        mPingButton = ((Button) findViewById(R.id.btn_ping));
        mPhotoButton = ((Button) findViewById(R.id.btn_send_photo));
        mDisconnectButton = ((Button) findViewById(R.id.btn_disconnect));
        mEditText = ((EditText) findViewById(R.id.et_tosend));

        ConfigurationBuilder builder = new ConfigurationBuilder(getApplicationContext(), this);
        builder
                .withBluetooth()
                .toConnect(builder.swipeLeftRight())
                .toSelect(builder.doubleTap())
                .toTransfer(builder.swipeUpDown())
                .toDisconnect(builder.syncBump())
                .select(Selection.Empty)
                .registerForLifecycleEvents(new ToastLifecycleListener(this))
                .registerPacketReceiver(this)
                .buildAndRegister();

        mSysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();
        mEditText.addTextChangedListener(new SysplaceTextWatcher(mSysplaceContext));

        mSysplaceContext.activate(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSysplaceContext.applicationResumed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSysplaceContext.applicationPaused();
    }

    public void ping(View view) {
        mSysplaceContext.send(new Packet("Ping!"));
    }

    public void switchToConnectedActivity(View view) {
        startActivity(new Intent(this, ConnectedActivity.class));
    }

    public void disconnect(View view) {
        mSysplaceContext.disconnect();
    }

    // IViewContext Stuff

    @Override
    public ViewWrapper getViewWrapper() {
        return ViewWrapper.wrap(findViewById(R.id.layout_main));
    }

    @Override
    public Point getDisplaySize() {
        //TODO: this only works properly in portrait mode. We have to subtract everything that
        // does not belong to the App (such as the StatusBar)
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    // IPacketReceiver Stuff

    @Override
    public boolean accept(Packet.PacketType type) {
        return true;
    }

    @Override
    public void receive(Packet packet) {
        switch (packet.getType()) {
            case ConnectionEstablished:
                mTextView.setText(R.string.connected_info);
                mTextView.setTextColor(Color.GREEN);
                mPingButton.setEnabled(true);
                mPhotoButton.setEnabled(true);
                mDisconnectButton.setEnabled(true);

                //TODO: turn this on again. was getting on my nerves >:(
                //((Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(700);

                break;
            case ConnectionLost:
                mTextView.setText(R.string.not_connected_info);
                mTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
                mPingButton.setEnabled(false);
                mPhotoButton.setEnabled(false);
                mDisconnectButton.setEnabled(false);
                ((Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(350);
                break;
            case PlainString:
                Toast.makeText(this, packet.getMessage(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}

