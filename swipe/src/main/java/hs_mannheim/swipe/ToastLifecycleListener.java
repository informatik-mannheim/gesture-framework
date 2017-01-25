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

package hs_mannheim.swipe;

import android.content.Context;
import android.widget.Toast;

import hs_mannheim.gestureframework.model.ILifecycleListener;

public class ToastLifecycleListener implements ILifecycleListener {

    private Context mContext;

    public ToastLifecycleListener(Context context) {
        mContext = context;
    }

    @Override
    public void onConnect() { }

    @Override
    public void onSelect() {

    }

    @Override
    public void onTransfer() {

    }

    @Override
    public void onDisconnect() {

    }
}
