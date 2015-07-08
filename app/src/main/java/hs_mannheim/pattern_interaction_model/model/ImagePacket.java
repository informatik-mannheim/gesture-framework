package hs_mannheim.pattern_interaction_model.model;

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
