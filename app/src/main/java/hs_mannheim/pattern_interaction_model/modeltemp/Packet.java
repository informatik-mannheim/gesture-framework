package hs_mannheim.pattern_interaction_model.modeltemp;

import java.io.Serializable;

public class Packet implements Serializable {
    private final String _type;
    private final String _message;

    public Packet(String type, String message) {
        _type = type;
        _message = message;
    }

    public String get_type() {
        return _type;
    }

    public String get_message() {
        return _message;
    }

    @Override
    public String toString() {
        return String.format("Message of type: %s containing: %s\n", _type, _message);
    }
}
