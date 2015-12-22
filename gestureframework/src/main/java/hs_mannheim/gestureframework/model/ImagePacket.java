package hs_mannheim.gestureframework.model;

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
