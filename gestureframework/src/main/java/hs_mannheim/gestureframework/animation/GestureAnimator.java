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

package hs_mannheim.gestureframework.animation;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class GestureAnimator implements Animator.AnimatorListener {

    protected View mView;
    protected Context mContext;
    protected List<Animator> mAnimatorQueue = new ArrayList<>();
    protected boolean mIsAnimationRunning;
    protected Bitmap mReplacementBitmap;

    public GestureAnimator(Context context, View view) {
        mView = view;
        mContext = context;
        registerAnimators();
    }

    public void setReplacementBitmap(Bitmap bitmap){
        mReplacementBitmap = bitmap;
    }

    public abstract void play();

    protected abstract void registerAnimators();


}
