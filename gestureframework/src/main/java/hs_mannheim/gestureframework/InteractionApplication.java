package hs_mannheim.gestureframework;

import android.app.Application;

import hs_mannheim.gestureframework.model.InteractionContext;

public class InteractionApplication extends Application {

    private InteractionContext mInteractionContext;

    public InteractionContext getInteractionContext() {
        return mInteractionContext;
    }

    public void setInteractionContext(InteractionContext mInteractionContext) {
        this.mInteractionContext = mInteractionContext;
    }
}
