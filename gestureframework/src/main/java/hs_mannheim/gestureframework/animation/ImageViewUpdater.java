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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.widget.ImageView;


public class ImageViewUpdater {

    private ImageView mImageView;
    private Bitmap mOriginalBitmap, mBitmapFrame;
    private Context mContext;

    public ImageViewUpdater(Context context, Bitmap bitmapFrame) {
        mContext = context;
        mBitmapFrame = bitmapFrame;
    }

    public void updateImageView(ImageView imageView, Bitmap newBitmap) {
        mImageView = imageView;
        mOriginalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
        Bitmap thumbnail = thumbnailUtils.extractThumbnail(newBitmap, mOriginalBitmap.getWidth(), mOriginalBitmap.getWidth());

        Bitmap combinedBitmap = Bitmap.createBitmap(mBitmapFrame.getWidth(), mBitmapFrame.getHeight(), mBitmapFrame.getConfig());
        Canvas canvas = new Canvas(combinedBitmap);
        canvas.drawBitmap(thumbnail, 0, 0, null);
        canvas.drawBitmap(mBitmapFrame, new Matrix(), null);

        mImageView.setImageDrawable(new BitmapDrawable(mContext.getResources(), combinedBitmap));
    }
}
