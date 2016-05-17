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
import android.view.View;

public abstract class TransitionAnimator extends GestureAnimator implements View.OnTouchListener {

    public TransitionAnimator(Context context, View view) {
        super(context, view);
    }

    public abstract void handleStart(GestureTransitionInfo info);

    public abstract void handleDuring(GestureTransitionInfo info);

    public abstract void handleEnd(GestureTransitionInfo info);
}
