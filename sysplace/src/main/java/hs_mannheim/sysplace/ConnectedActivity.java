package hs_mannheim.sysplace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

import hs_mannheim.gestureframework.animation.GestureAnimator;
import hs_mannheim.gestureframework.messaging.IPacketReceiver;
import hs_mannheim.gestureframework.messaging.ImagePacket;
import hs_mannheim.gestureframework.messaging.Packet;
import hs_mannheim.gestureframework.messaging.SerializableImage;
import hs_mannheim.gestureframework.model.ILifecycleListener;
import hs_mannheim.gestureframework.model.IViewContext;
import hs_mannheim.gestureframework.model.InteractionApplication;
import hs_mannheim.gestureframework.model.LifecycleEvent;
import hs_mannheim.gestureframework.model.ViewWrapper;
import hs_mannheim.gestureframework.model.Selection;
import hs_mannheim.gestureframework.model.SysplaceContext;
import hs_mannheim.sysplace.animations.FlipSelectAnimator;
import hs_mannheim.sysplace.animations.FlyInAndLowerAnimator;

public class ConnectedActivity extends AppCompatActivity implements IViewContext, ILifecycleListener, IPacketReceiver {

    private static int PICK_IMAGE = 1;
    private SysplaceContext mSysplaceContext;
    private final String TAG = "[ConnectedActivity]";
    private ViewWrapper mViewWrapper;
    private GestureAnimator mReceiveAnimator, mSelectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        mViewWrapper = new ViewWrapper(findViewById(R.id.imgView));

        mSysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();

        mSysplaceContext.registerForLifecycleEvents(this);
        mSysplaceContext.registerPacketReceiver(this);
        mSysplaceContext.updateViewContext(LifecycleEvent.SELECT, this);
        mSysplaceContext.updateViewContext(LifecycleEvent.TRANSFER, this);

        mSelectAnimator = new FlipSelectAnimator(this, mViewWrapper.getView());
        mReceiveAnimator = new FlyInAndLowerAnimator(this, mViewWrapper.getView());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Fires image chooser intent.
     *
     */
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    /**
     * Callback on image chooser Activity. Transforms to Bitmap and adds to ImageView
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
                    Toast.makeText(this, "Something went wrong... data returned null", Toast.LENGTH_LONG).show();
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

    /**
     * Combines the chosen image file with the polaroid frame and displays the resulting Bitmap
     * in the ImageView
     * @param chosenBitmap
     */
    private void updatePicture(Bitmap chosenBitmap) {

        /*ImageView imgView = (ImageView) mViewWrapper.getView();
        Bitmap polaroid = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
        Bitmap polaroidFrame = BitmapFactory.decodeResource(getResources(), R.drawable.polaroid_frame);

        ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
        Bitmap thumbnail = thumbnailUtils.extractThumbnail(chosenImage, polaroid.getWidth(), polaroid.getWidth());

        Bitmap combinedBitmap = Bitmap.createBitmap(polaroidFrame.getWidth(), polaroidFrame.getHeight(), polaroidFrame.getConfig());
        Canvas canvas = new Canvas(combinedBitmap);
        canvas.drawBitmap(thumbnail, 0, 0, null);
        canvas.drawBitmap(polaroidFrame, new Matrix(), null);

        imgView.setImageDrawable(new BitmapDrawable(getResources(), combinedBitmap));*/
        mSelectAnimator.setReplacementBitmap(chosenBitmap);
        mSelectAnimator.play();
        //mReceiveAnimator.setReplacementBitmap(chosenBitmap);
        //mReceiveAnimator.play();
        mSysplaceContext.select(new Selection(new ImagePacket(new SerializableImage(chosenBitmap))));
    }

    @Override
    public ViewWrapper getViewWrapper() {
        return mViewWrapper;
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
        Bitmap receivedBitmap = ((ImagePacket) packet).getImage().getImage();
        mReceiveAnimator.setReplacementBitmap(receivedBitmap);
        mReceiveAnimator.play();
        mSysplaceContext.select(new Selection(new ImagePacket(new SerializableImage(receivedBitmap))));
    }

    @Override
    public boolean accept(Packet.PacketType type) {
        return type == Packet.PacketType.Image;
    }
}
