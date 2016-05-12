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

import java.io.Serializable;

import hs_mannheim.gestureframework.connection.IConnection;

/**
 * Packet that contains an image and can be send through an
 * {@link IConnection}. Don't delete it, even if it is not used
 * right now.
 */
@SuppressWarnings("unused")
public class ImagePacket extends Packet implements Serializable {
    private SerializableImage mImage;

    public ImagePacket(SerializableImage image) {
        super(PacketType.Image, "Image");
        mImage = image;
    }

    public SerializableImage getImage() {
        return mImage;
    }
}
