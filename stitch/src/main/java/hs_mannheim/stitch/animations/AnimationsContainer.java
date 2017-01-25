/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *     Hochschule Mannheim University of Applied Sciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package hs_mannheim.stitch.animations;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import hs_mannheim.stitch.R;


public class AnimationsContainer implements OnAnimationStoppedListener {
    public int FPS = 6;  // animation FPS

    // single instance procedures
    private static AnimationsContainer mInstance;
    private List<OnAnimationStoppedListener> mListeners = new ArrayList<>();

    private AnimationsContainer() {
    }

    public static AnimationsContainer getInstance() {
        if (mInstance == null)
            mInstance = new AnimationsContainer();
        return mInstance;
    }

    // animation splash screen frames
    private int[] mSplashAnimFrames = {
            R.drawable.polaroid_pick,
            R.drawable.polaroid_pick_an,
            R.drawable.polaroid_pick_ano,
            R.drawable.polaroid_pick_anot,
            R.drawable.polaroid_pick_anoth,
            R.drawable.polaroid_pick_anothe,
            R.drawable.polaroid_pick_another_exclamation,
            R.drawable.polaroid_pick_another,
            R.drawable.polaroid_pick_another_question};


    /**
     * @param imageView
     * @return progress dialog animation
     */
    public FramesSequenceAnimation createProgressDialogAnim(ImageView imageView) {
        FramesSequenceAnimation animation = new FramesSequenceAnimation(imageView, mSplashAnimFrames, FPS);
        animation.registerListener(this);
        return animation;
    }

    /**
     * @param imageView
     * @return splash screen animation
     */
    public FramesSequenceAnimation createSplashAnim(ImageView imageView) {
        FramesSequenceAnimation animation = new FramesSequenceAnimation(imageView, mSplashAnimFrames, FPS);
        animation.registerListener(this);
        return animation;
    }

    @Override
    public void animationStopped() {
        for (OnAnimationStoppedListener listener : mListeners) {
            listener.animationStopped();
        }
    }

    public void registerListener(OnAnimationStoppedListener listener) {
        if(!mListeners.contains(listener)){
            mListeners.add(listener);
        }
    }

    /**
     * AnimationPlayer. Plays animation frames sequence in loop
     */
    public class FramesSequenceAnimation {
        private int[] mFrames; // animation frames
        private int mIndex; // current frame
        private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
        private boolean mIsRunning; // true if the animation currently running. prevents starting the animation twice
        private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
        private Handler mHandler;
        private int mDelayMillis;
        private OnAnimationStoppedListener mOnAnimationStoppedListener;

        private Bitmap mBitmap = null;
        private BitmapFactory.Options mBitmapOptions;

        public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps) {
            mHandler = new Handler();
            mFrames = frames;
            mIndex = -1;
            mSoftReferenceImageView = new SoftReference(imageView);
            mShouldRun = false;
            mIsRunning = false;
            mDelayMillis = 1000 / fps;

            imageView.setImageResource(mFrames[0]);

            // use in place bitmap to save GC work (when animation images are the same size & type)
            if (Build.VERSION.SDK_INT >= 11) {
                Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                int width = bmp.getWidth();
                int height = bmp.getHeight();
                Bitmap.Config config = bmp.getConfig();
                mBitmap = Bitmap.createBitmap(width, height, config);
                mBitmapOptions = new BitmapFactory.Options();
                // setup bitmap reuse options.
                mBitmapOptions.inBitmap = mBitmap;
                mBitmapOptions.inMutable = true;
                mBitmapOptions.inSampleSize = 1;
            }
        }

        public void registerListener(OnAnimationStoppedListener listener) {
            mOnAnimationStoppedListener = listener;
        }

        private int getNext() {
            mIndex++;
            if (mIndex == mFrames.length - 1) {
                mShouldRun = false;
            }
            return mFrames[mIndex];
        }

        public synchronized void start() {
            mShouldRun = true;
            if (mIsRunning)
                return;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = mSoftReferenceImageView.get();
                    if (!mShouldRun || imageView == null) {
                        mIsRunning = false;
                        if (mOnAnimationStoppedListener != null) {
                            mOnAnimationStoppedListener.animationStopped();
                        }
                        return;
                    }

                    mIsRunning = true;
                    mHandler.postDelayed(this, mDelayMillis);

                    if (imageView.isShown()) {
                        int imageRes = getNext();
                        if (mBitmap != null) {
                            Bitmap bitmap = null;
                            try {
                                bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, mBitmapOptions);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                imageView.setImageResource(imageRes);
                                mBitmap.recycle();
                                mBitmap = null;
                            }
                        } else {
                            imageView.setImageResource(imageRes);
                        }
                    }

                }
            };
            mHandler.post(runnable);
        }
    }
}
