package hs_mannheim.gestureframework.messaging;

import hs_mannheim.gestureframework.connection.IConnection;

/**
 * Packet that contains an image and can be send through an
 * {@link IConnection}. Don't delete it, even if it is not used
 * right now.
 */
@SuppressWarnings("unused")
public class ImagePacket extends Packet {
    private SerializableImage mImage;

    public ImagePacket(SerializableImage image) {
        super(PacketType.Image, "Image");
        mImage = image;
    }

    public SerializableImage getImage() {
        return mImage;
    }
}
