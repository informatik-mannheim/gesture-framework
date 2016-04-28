package hs_mannheim.sysplace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

import hs_mannheim.gestureframework.InteractionApplication;
import hs_mannheim.gestureframework.animation.GestureAnimation;
import hs_mannheim.gestureframework.animation.PostCardFlipAnimationSend;
import hs_mannheim.gestureframework.animation.PostcardFlipAnimationReceive;
import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.model.GestureDetector;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.LifecycleEvent;
import hs_mannheim.gestureframework.model.SysplaceContext;
import hs_mannheim.gestureframework.model.MultipleTouchView;

public class ConnectedActivity extends AppCompatActivity implements  IViewContext {

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
        SysplaceContext sysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();

        sysplaceContext.updateViewContextAll(this);

        //TODO: UGLY AF.. Hardcode SwipeDetector in or let GestureDetector fire events with more information.
        GestureDetector gestureDetector = sysplaceContext.getGestureManager().getGestureDetectorFor(LifecycleEvent.TRANSFER);
        if(gestureDetector instanceof SwipeDetector){
            swipeDetector = (SwipeDetector) gestureDetector;
            swipeDetector.addSwipeListener(swipeHandler);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        swipeDetector.removeSwipeListener(swipeHandler);
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
    public MultipleTouchView getInteractionView() {
        return new MultipleTouchView(findViewById(R.id.imgView));
    }

    @Override
    public Point getDisplaySize() {
        //TODO: implement
        return null;
    }
}
