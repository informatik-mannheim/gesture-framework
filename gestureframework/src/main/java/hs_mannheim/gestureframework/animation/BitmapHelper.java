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
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class BitmapHelper {

    private static final String TAG = "[BitmapHelper]";

    /**
     * Combines the two given Bitmaps and adds it to the given ImageView.
     * @param context
     * @param imageView
     * @param newBitmap
     * @param frame
     */
    public static void updateImageView(Context context, ImageView imageView, Bitmap newBitmap, Bitmap frame) {
        Bitmap originalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
        Bitmap thumbnail = thumbnailUtils.extractThumbnail(newBitmap, originalBitmap.getWidth(), originalBitmap.getWidth());

        Bitmap combinedBitmap = Bitmap.createBitmap(frame.getWidth(), frame.getHeight(), frame.getConfig());
        Canvas canvas = new Canvas(combinedBitmap);
        canvas.drawBitmap(thumbnail, 0, 0, null);
        canvas.drawBitmap(frame, new Matrix(), null);

        imageView.setImageDrawable(new BitmapDrawable(context.getResources(), combinedBitmap));
    }

    /**
     * Returns a Bitmap from a given InputStream with a size under the given max dimensions
     * @param inputStream
     * @param maxDims
     * @return
     * @throws java.io.IOException
     */
    public static Bitmap decodeBitmapFromInputStream(InputStream inputStream,
                                                     int maxDims) throws java.io.IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        InputStream sizeCheckStream = new ByteArrayInputStream(baos.toByteArray());
        InputStream bitmapStream = new ByteArrayInputStream(baos.toByteArray());

        ImageDimensions currentDimensions = getImageDimensionsFromInputStream(sizeCheckStream);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(currentDimensions, maxDims);

        return BitmapFactory.decodeStream(bitmapStream, new Rect(0, 0, 512, 386), options);
    }

    private static int calculateInSampleSize(
            ImageDimensions imageDims, int maxDims) {
        // Raw height and width of image
        final int height = imageDims.getImageHeight();
        final int width = imageDims.getImageWidth();
        int inSampleSize = 1;

        if (height > maxDims || width > maxDims) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= maxDims
                    || (halfWidth / inSampleSize) >= maxDims) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }



    private static ImageDimensions getImageDimensionsFromInputStream(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, new Rect(0, 0, 512, 386), options);
        return new ImageDimensions(options.outWidth, options.outHeight) ;
    }
}
