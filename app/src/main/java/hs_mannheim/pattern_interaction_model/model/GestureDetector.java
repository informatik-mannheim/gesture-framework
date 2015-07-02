package hs_mannheim.pattern_interaction_model.model;

public abstract class GestureDetector {

    private GestureEventListener mListener;

    public void registerGestureEventListener(GestureEventListener listener) {
        this.mListener = listener;
    }

    protected void fireGestureDetected() {
        if(mListener != null) {
            mListener.onGestureDetected();
        }
    }

    public interface GestureEventListener {
        void onGestureDetected();
    }
}
