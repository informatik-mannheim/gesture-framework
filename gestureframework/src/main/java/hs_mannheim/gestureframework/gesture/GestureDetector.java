/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework.gesture;

import hs_mannheim.gestureframework.model.IViewContext;

public abstract class GestureDetector {

    private GestureEventListener mListener;
    protected IViewContext mViewContext;

    public GestureDetector(IViewContext viewContext) {
        setViewContext(viewContext);
    }

    public void registerGestureEventListener(GestureEventListener listener) {
        this.mListener = listener;
    }

    protected void fireGestureDetected() {
        if(mListener != null) {
            mListener.onGestureDetected(this);
        }
    }

    public void setViewContext(IViewContext viewContext) {
        mViewContext = viewContext;
    }

    public interface GestureEventListener {
        void onGestureDetected(GestureDetector gestureDetector);
    }
}