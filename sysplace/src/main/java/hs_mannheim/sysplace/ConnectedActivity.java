package hs_mannheim.sysplace;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

import hs_mannheim.gestureframework.InteractionApplication;
import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.animation.PostCardFlipAnimationSend;
import hs_mannheim.gestureframework.animation.PostcardFlipAnimationReceive;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;
import hs_mannheim.gestureframework.model.GestureContext;
import hs_mannheim.gestureframework.model.GestureDetector;
import hs_mannheim.gestureframework.model.GestureManager;
import hs_mannheim.gestureframework.model.IPacketReceiver;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionContext;
import hs_mannheim.gestureframework.model.Packet;
import hs_mannheim.gestureframework.model.PacketType;

public class ConnectedActivity extends AppCompatActivity implements  IViewContext, GestureManager.GestureListener{

    private static int PICK_IMAGE = 1;
    private ImageView imgView;
    private GestureAnimation sendAnimation, receiveAnimation;
    private SwipeHandler swipeHandler;
    private ReceiveHandler receiveHandler;
    private SwipeDetector swipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        imgView = (ImageView) findViewById(R.id.imgView);

        //TODO: Set animations beforehand
        this.sendAnimation = new PostCardFlipAnimationSend(this, imgView);
        this.receiveAnimation = new PostcardFlipAnimationReceive(this, imgView);

        swipeHandler = new SwipeHandler(sendAnimation);
        receiveHandler = new ReceiveHandler(receiveAnimation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InteractionContext interactionContext = ((InteractionApplication) getApplicationContext()).getInteractionContext();
        interactionContext.getPostOffice().register(receiveHandler);
        interactionContext.updateViewContextAll(this);

        //TODO: UGLY AF.. Hardcode SwipeDetector in or let GestureDetector fire events with more information.
        GestureDetector gestureDetector = interactionContext.getGestureManager().getGestureDetector(GestureContext.TRANSFER);
        if(gestureDetector instanceof SwipeDetector){
            swipeDetector = (SwipeDetector) gestureDetector;
            swipeDetector.addSwipeListener(swipeHandler);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        swipeDetector.removeSwipeListener(swipeHandler);
        ((InteractionApplication) getApplicationContext()).getInteractionContext().getPostOffice().unregister(receiveHandler);
    }

    /**
     * Fires image chooser intent. TODO: Shouldn't be activated by a button
     * @param view
     */
    public void chooseImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    /**
     * Callback on image chooser Activity. Transforms to Bitmap and adds to imageview
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK ) {

                if ( data == null){
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    return;
                }

                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgView = (ImageView) findViewById(R.id.imgView);
                imgView.setImageBitmap(bitmap);

            } else {
                Toast.makeText(this, "You haven't picked an Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View getInteractionView() {
        //TODO: implement
        return null;
    }

    @Override
    public Point getDisplaySize() {
        //TODO: implement
        return null;
    }

    @Override
    public void onGestureDetected() {

    }

    @Override
    public void onSwipeDetected(SwipeEvent event) {

    }

    @Override
    public void onSwiping(TouchPoint touchPoint) {

    }

    @Override
    public void onSwipeStart(TouchPoint touchPoint, View view) {

    }

    @Override
    public void onSwipeEnd(TouchPoint touchPoint) {

    }
}
