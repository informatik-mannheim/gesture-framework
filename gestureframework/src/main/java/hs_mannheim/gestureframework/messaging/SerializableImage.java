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

package hs_mannheim.gestureframework.messaging;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableImage implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int NO_IMAGE = -1;

    private Bitmap mImage;
    private String TAG = "[SerializableImage]";

    public SerializableImage(Bitmap bitmap) {
        setImage(bitmap);
    }

    public Bitmap getImage() {
        return mImage;
    }

    private void setImage(Bitmap image) {
        mImage = image;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Log.d(TAG, "serializing...");
        if (mImage != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mImage.compress(Bitmap.CompressFormat.JPEG, 20, stream);
            byte[] imageByteArray = stream.toByteArray();
            out.writeInt(imageByteArray.length);
            out.write(imageByteArray);
        } else {
            out.writeInt(NO_IMAGE);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        final int length = in.readInt();

        if (length != NO_IMAGE) {
            final byte[] imageByteArray = new byte[length];
            in.readFully(imageByteArray);
            mImage = BitmapFactory.decodeByteArray(imageByteArray, 0, length);
        }
    }
}