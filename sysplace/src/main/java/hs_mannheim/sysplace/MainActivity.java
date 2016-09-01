package hs_mannheim.sysplace;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
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
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionApplication;
import hs_mannheim.gestureframework.model.Selection;
import hs_mannheim.gestureframework.model.SysplaceContext;
import hs_mannheim.gestureframework.model.ViewWrapper;
import hs_mannheim.sysplace.animations.PlugAnimator;
import hs_mannheim.sysplace.animations.SocketAnimator;

public class MainActivity extends AppCompatActivity implements IViewContext, IPacketReceiver, SwipeDetector.SwipeEventListener {
    private static final String TAG = "[Main Activity]";

    private Button mPhotoButton;
    private SysplaceContext mSysplaceContext;
    private PlugAnimator mPlugAnimator;
    private SocketAnimator mSocketAnimator;
    private boolean mIsConnectionEstablished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "App starts");

        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mPhotoButton = ((Button) findViewById(R.id.btn_send_photo)); //TODO: get rid of this after transition works

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



        mSysplaceContext.activate(this);

        mSysplaceContext.registerForSwipeEvents(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View plugView = findViewById(R.id.plug), plugPinsView = findViewById(R.id.plug_pins),
                socketView = findViewById(R.id.socket);
        mPlugAnimator = new PlugAnimator(this, plugView, getDisplaySize());
        mSocketAnimator = new SocketAnimator(this, socketView, getDisplaySize());
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

    public void switchToConnectedActivity() {
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
                //mTextView.setText(R.string.connected_info);
                //mTextView.setTextColor(Color.GREEN);
                mPhotoButton.setEnabled(true);

                //TODO: turn this on again. was getting on my nerves >:(
                //((Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(700);

                mIsConnectionEstablished = true;

                if (mSwipeOrientation == SwipeEvent.Orientation.WEST) {
                    mSocketAnimator.plugIn();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchToConnectedActivity();
                        }
                    }, 3000);
                } else if (mSwipeOrientation == SwipeEvent.Orientation.EAST) {
                    mPlugAnimator.plugIn();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchToConnectedActivity();
                        }
                    }, 3000);
                }

                break;
            case ConnectionLost:
                //mTextView.setText(R.string.not_connected_info);
                //mTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light, null));
                mPhotoButton.setEnabled(false);
                ((Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(350);
                break;
            case PlainString:
                Toast.makeText(this, packet.getMessage(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private SwipeEvent.Orientation mSwipeOrientation;
    private boolean isPeakedIn;

    @Override
    public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event) {
        mSwipeOrientation = event.getOrientation();

        if (!isPeakedIn) {
            if (mSwipeOrientation == SwipeEvent.Orientation.WEST) {
                mSocketAnimator.play();
            } else if (mSwipeOrientation == SwipeEvent.Orientation.EAST) {
                mPlugAnimator.play();
            }
            isPeakedIn = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mIsConnectionEstablished) {
                        if (mSwipeOrientation == SwipeEvent.Orientation.WEST) {
                            mSocketAnimator.retreat();
                        } else if (mSwipeOrientation == SwipeEvent.Orientation.EAST) {
                            mPlugAnimator.retreat();
                        }

                        isPeakedIn = false;
                    }
                }
            }, 7000);
        }
    }

    @Override
    public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }

    @Override
    public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {

    }

    @Override
    public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }
}

