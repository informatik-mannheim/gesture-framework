package hs_mannheim.gestureframework.model;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

public class GestureManager implements GestureDetector.GestureEventListener,
        SwipeDetector.SwipeEventListener {

    private static final String TAG = "[GestureManager]";
    private GestureDetector mConnectDetector, mSelectDetector, mTransferDetector, mDisconnectDetector;

    private final List<ILifecycleListener> mLifeCycleEventListeners = new ArrayList<>();

    private IViewContext mViewContext;

    /**
     * @param connectDetector    nullable
     * @param selectDetector     nullable
     * @param transferDetector   nullable
     * @param disconnectDetector nullable
     */
    public GestureManager(GestureDetector connectDetector, GestureDetector selectDetector, GestureDetector transferDetector, GestureDetector disconnectDetector) {
        setGestureDetector(LifecycleEvent.CONNECT, connectDetector);
        setGestureDetector(LifecycleEvent.SELECT, selectDetector);
        setGestureDetector(LifecycleEvent.TRANSFER, transferDetector);
        setGestureDetector(LifecycleEvent.DISCONNECT, disconnectDetector);

        mConnectDetector.registerGestureEventListener(this);
        mSelectDetector.registerGestureEventListener(this);
        mTransferDetector.registerGestureEventListener(this);
        mDisconnectDetector.registerGestureEventListener(this);
    }

    public void setViewContext(LifecycleEvent lifecycleEvent, IViewContext viewContext) {
        switch (lifecycleEvent) {
            case CONNECT:
                mConnectDetector.setViewContext(viewContext);
            case SELECT:
                mSelectDetector.setViewContext(viewContext);
            case TRANSFER:
                mTransferDetector.setViewContext(viewContext);
            case DISCONNECT:
                mDisconnectDetector.setViewContext(viewContext);
        }
    }

    public void setViewContextAll(IViewContext viewContext) {
        mConnectDetector.setViewContext(viewContext);
        mSelectDetector.setViewContext(viewContext);
        mTransferDetector.setViewContext(viewContext);
        mDisconnectDetector.setViewContext(viewContext);
    }

    /**
     * Returns the currently registered {@link GestureDetector} for a given {@link LifecycleEvent}.
     *
     * @param lifecycleEvent The queried {@link LifecycleEvent}
     * @return The desired {@link GestureDetector}. Might be a {@link VoidGestureDetector}!
     */
    public GestureDetector getGestureDetectorFor(LifecycleEvent lifecycleEvent) {
        switch (lifecycleEvent) {
            case CONNECT:
                return mConnectDetector;
            case SELECT:
                return mSelectDetector;
            case TRANSFER:
                return mTransferDetector;
            case DISCONNECT:
                return mDisconnectDetector;
            default:
                return new VoidGestureDetector(mViewContext);
        }
    }

    /**
     * Registers a new {@link GestureDetector} for a specific {@link LifecycleEvent}.
     *
     * @param lifecycleEvent  The context (Connect, Select, Transfer, Disconnect) for which to
     *                        register a new listener (if there was an old one, it will be replaced)
     * @param gestureDetector The GestureDetector to register to the given GestureContext
     */
    public void setGestureDetector(LifecycleEvent lifecycleEvent, GestureDetector gestureDetector) {
        if(gestureDetector == null) {
            gestureDetector = new VoidGestureDetector(mViewContext);
        }

        switch (lifecycleEvent) {
            case CONNECT:
                mConnectDetector = gestureDetector;
                mConnectDetector.registerGestureEventListener(this);
                break;
            case SELECT:
                mSelectDetector = gestureDetector;
                mSelectDetector.registerGestureEventListener(this);
                break;
            case TRANSFER:
                mTransferDetector = gestureDetector;
                mTransferDetector.registerGestureEventListener(this);
                break;
            case DISCONNECT:
                mDisconnectDetector = gestureDetector;
                mDisconnectDetector.registerGestureEventListener(this);
                break;
        }
    }

    /**
     * Registers a listener for all LifecycleEvents.
     * TODO: make this Threadsafe
     * @param lifecycleListener the listener to register
     */
    public void registerLifecycleListener(ILifecycleListener lifecycleListener) {
        mLifeCycleEventListeners.add(lifecycleListener);
    }

    /**
     * Unregisters a listener for all LifecycleEvents.
     * TODO: make this Threadsafe
     * @param lifecycleListener the listener to register
     */
    public void unregisterLifecycleListener(ILifecycleListener lifecycleListener) {
        if (mLifeCycleEventListeners.contains(lifecycleListener)) {
            mLifeCycleEventListeners.remove(lifecycleListener);
        }
    }

    @Override
    public void onGestureDetected(GestureDetector gestureDetector) {
        for (ILifecycleListener gestureListener : mLifeCycleEventListeners) {
            if (gestureDetector.equals(mConnectDetector)) {
                gestureListener.onConnect();
                Log.d(TAG, "Connect Gesture fired");
            } else if (gestureDetector.equals(mSelectDetector)) {
                gestureListener.onSelect();
            } else if (gestureDetector.equals(mTransferDetector)) {
                gestureListener.onTransfer();
            } else if (gestureDetector.equals(mDisconnectDetector)) {
                gestureListener.onDisconnect();
            }
        }
    }

    @Override
    public void onSwipeDetected(SwipeDetector swipeDetector, SwipeEvent event) {

    }

    @Override
    public void onSwiping(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }

    @Override
    public void onSwipeStart(SwipeDetector swipeDetector, TouchPoint touchPoint, View view) {

    }

    @Override
    public void onSwipeEnd(SwipeDetector swipeDetector, TouchPoint touchPoint) {

    }
}
