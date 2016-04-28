package hs_mannheim.pattern_interaction_model;

import android.app.Application;

import hs_mannheim.gestureframework.model.SysplaceContext;

public class InteractionApplication extends Application {

private SysplaceContext mSysplaceContext;

    public SysplaceContext getInteractionContext() {
        return mSysplaceContext;
    }

    public void setInteractionContext(SysplaceContext mSysplaceContext) {
        this.mSysplaceContext = mSysplaceContext;
    }
}
