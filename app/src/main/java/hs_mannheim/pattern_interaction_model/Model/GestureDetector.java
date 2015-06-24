package hs_mannheim.pattern_interaction_model.Model;

public abstract class GestureDetector {

    private GestureEventListener mListener;

    public void registerGestureEventListener(GestureEventListener listener) {
        this.mListener = listener;
    }

    protected void onGestureDetected() {
        if(mListener != null) {
            mListener.onGestureDetected();
        }
    }

    public interface GestureEventListener {
        void onGestureDetected();
    }
}
