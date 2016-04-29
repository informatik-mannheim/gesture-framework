package hs_mannheim.sysplace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.animation.PostCardFlipAnimationSend;
import hs_mannheim.gestureframework.animation.PostcardFlipAnimationReceive;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.ImagePacket;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.messaging.SerializableImage;
import hs_mannheim.gestureframework.model.ILifecycleListener;
import hs_mannheim.gestureframework.model.ISysplaceContext;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionApplication;
import hs_mannheim.gestureframework.model.LifecycleEvent;
import hs_mannheim.gestureframework.model.MultipleTouchView;
import hs_mannheim.gestureframework.model.Selection;
import hs_mannheim.gestureframework.model.SysplaceContext;

public class ConnectedActivity extends AppCompatActivity implements IViewContext, ILifecycleListener, IPacketReceiver {

    private static int PICK_IMAGE = 1;
    private ImageView imgView;
    private GestureAnimation sendAnimation, receiveAnimation;
    private SysplaceContext mSysplaceContext;

    //private SwipeHandler swipeHandler;
    //private SwipeDetector swipeDetector;

    private final String TAG = "[ConnectedActivity]";
    private MultipleTouchView mViewContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        imgView = (ImageView) findViewById(R.id.imgView);
        mViewContext = new MultipleTouchView(findViewById(R.id.imgView));

        mSysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();

        mSysplaceContext.registerForLifecycleEvents(this);
        mSysplaceContext.registerPacketReceiver(this);
        mSysplaceContext.getGestureManager().setViewContext(LifecycleEvent.SELECT, this);
        mSysplaceContext.getGestureManager().setViewContext(LifecycleEvent.TRANSFER, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        SysplaceContext sysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();

        sysplaceContext.updateViewContextAll(this);

        //TODO: UGLY AF.. Hardcode SwipeDetector in or let GestureDetector fire events with more information.
        GestureDetector gestureDetector = sysplaceContext.getGestureManager().getGestureDetectorFor(LifecycleEvent.TRANSFER);
        if(gestureDetector instanceof SwipeDetector){
            swipeDetector = (SwipeDetector) gestureDetector;
            swipeDetector.addSwipeListener(swipeHandler);
        }
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        // swipeDetector.removeSwipeListener(swipeHandler);
    }

    /**
     * Fires image chooser intent. TODO: Shouldn't be activated by a button
     *
     * @param view
     */
    public void chooseImage(View view) {
        chooseImage();
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    /**
     * Callback on image chooser Activity. Transforms to Bitmap and adds to imageview
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

                if (data == null) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    return;
                }

                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                updatePicture(bitmap);

                // can not set animations before the picture data is ready.
               // sendAnimation = new PostCardFlipAnimationSend(this, imgView);
               // receiveAnimation = new PostcardFlipAnimationReceive(this, imgView);

               // swipeHandler = new SwipeHandler(sendAnimation);

            } else {
                Toast.makeText(this, "You haven't picked an Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void updatePicture(Bitmap bitmap) {
        imgView = (ImageView) findViewById(R.id.imgView);
        imgView.setImageBitmap(bitmap);
        mSysplaceContext.select(new Selection(new ImagePacket(new SerializableImage(bitmap))));
    }

    @Override
    public MultipleTouchView getMultipleTouchView() {
        return mViewContext;
    }

    @Override
    public Point getDisplaySize() {
        //TODO: implement
        return null;
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onSelect() {
        chooseImage();
    }

    @Override
    public void onTransfer() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void receive(Packet packet) {
        updatePicture(((ImagePacket) packet).getImage().getImage());
    }

    @Override
    public boolean accept(Packet.PacketType type) {
        return type == Packet.PacketType.Image;
    }
}
