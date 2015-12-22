package hs_mannheim.gestureframework.model;

public abstract class GestureDetector {

    private GestureEventListener mListener;
    protected IViewContext mViewContext;

    public GestureDetector(IViewContext viewContext) {
        setViewContext(viewContext);
    }

    public void registerGestureEventListener(GestureEventListener listener) {
        this.mListener = listener;
    }

    protected void fireGestureDetected() {
        if(mListener != null) {
            mListener.onGestureDetected();
        }
    }

    public void setViewContext(IViewContext viewContext) {
        mViewContext = viewContext;
    }

    public interface GestureEventListener {
        void onGestureDetected();
    }
}