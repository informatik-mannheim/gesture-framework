package hs_mannheim.gestureframework;

import android.app.Application;

import hs_mannheim.gestureframework.model.SysplaceContext;

public class InteractionApplication extends Application {

    private SysplaceContext mSysplaceContext;

    public SysplaceContext getSysplaceContext() {
        return mSysplaceContext;
    }

    public void setInteractionContext(SysplaceContext mSysplaceContext) {
        this.mSysplaceContext = mSysplaceContext;
    }
}
