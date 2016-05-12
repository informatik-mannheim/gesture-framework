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

package hs_mannheim.gestureframework.model;

import android.database.Observable;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

/**
 * Listens for TouchEvents on a View and distributes it to 0-n
 * {@link android.view.View.OnTouchListener} instances as a normal {@link View} only allows one
 * OnTouchListener at a time. I hope this isn't problematic in some cases? Is there a reason why
 * you can have only one OnTouchListener per View?
 */
public class ViewWrapper extends Observable<View.OnTouchListener> implements View.OnTouchListener {
    private final View mView;
    private final static Set<ViewWrapper> mWrappers = new HashSet<>();

    public ViewWrapper(View view)  {
        mView = view;
        mView.setOnTouchListener(this);
    }

    public static ViewWrapper wrap(View view) {
        for(ViewWrapper wrapper : mWrappers) {
            if(wrapper.getView().equals(view)) {
                return wrapper;
            }
        }

        ViewWrapper wrapper = new ViewWrapper(view);
        mWrappers.add(wrapper);
        return wrapper;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        for (View.OnTouchListener listener : mObservers) {
            listener.onTouch(view, motionEvent);
        }

        return true;
    }

    public View getView() {
        return mView;
    }
}
