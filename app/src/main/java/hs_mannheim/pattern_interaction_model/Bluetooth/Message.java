package hs_mannheim.pattern_interaction_model.Bluetooth;

import java.io.Serializable;

public class Message implements Serializable {
    private String mMessage;

    public Message(String message) {
        this.mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
