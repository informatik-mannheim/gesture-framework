package hs_mannheim.gestureframework.model;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hs_mannheim.gestureframework.gesture.swipe.SwipeDetector;
import hs_mannheim.gestureframework.gesture.swipe.SwipeEvent;
import hs_mannheim.gestureframework.gesture.swipe.TouchPoint;

public class GestureManager implements GestureDetector.GestureEventListener,
        SwipeDetector.SwipeEventListener {

    private final Map<LifecycleEvent, GestureDetector> mDetectors = new HashMap<>();
    private final List<ILifecycleListener> mLifeCycleEventListeners = new ArrayList<>();

    /**
     * Builds a new instance of the {@link GestureManager} and registers a {@link GestureDetector}
     * for every {@link LifecycleEvent}.
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
    }

    /**
     * Set a {@link IViewContext} for a specific LifecycleEvent registered.
     * @param lifecycleEvent The {@link LifecycleEvent} to set the {@link IViewContext} for.
     * @param viewContext The {@link IViewContext} to set.
     */
    public void setViewContext(LifecycleEvent lifecycleEvent, IViewContext viewContext) {
        mDetectors.get(lifecycleEvent).setViewContext(viewContext);
    }

    /**
     * Set the same {@link IViewContext} on every GestureDetector registered.
     * @param viewContext The {@link IViewContext} to set.
     */
    public void setViewContextAll(IViewContext viewContext) {
        for (GestureDetector detector : mDetectors.values()) {
            detector.setViewContext(viewContext);
        }
    }

    /**
     * Returns the currently registered {@link GestureDetector} for a given {@link LifecycleEvent}.
     *
     * @param lifecycleEvent The queried {@link LifecycleEvent}
     * @return The desired {@link GestureDetector}. Might be a {@link VoidGestureDetector}!
     */
    public GestureDetector getGestureDetectorFor(LifecycleEvent lifecycleEvent) {
        return mDetectors.get(lifecycleEvent);
    }

    /**
     * Registers a new {@link GestureDetector} for a specific {@link LifecycleEvent}.
     *
     * @param lifecycleEvent  The context (Connect, Select, Transfer, Disconnect) for which to
     *                        register a new listener (if there was an old one, it will be replaced)
     * @param gestureDetector The GestureDetector to register to the given GestureContext
     */
    public void setGestureDetector(LifecycleEvent lifecycleEvent, GestureDetector gestureDetector) {
        if (gestureDetector == null) {
            gestureDetector = new VoidGestureDetector(null);
        }

        gestureDetector.registerGestureEventListener(this);
        mDetectors.put(lifecycleEvent, gestureDetector);
    }

    /**
     * Registers a listener for all LifecycleEvents.
     * TODO: make this Threadsafe
     *
     * @param lifecycleListener the listener to register
     */
    public void registerLifecycleListener(ILifecycleListener lifecycleListener) {
        mLifeCycleEventListeners.add(lifecycleListener);
    }

    /**
     * Unregisters a listener for all LifecycleEvents.
     * TODO: make this Threadsafe
     *
     * @param lifecycleListener the listener to register
     */
    public void unregisterLifecycleListener(ILifecycleListener lifecycleListener) {
        if (mLifeCycleEventListeners.contains(lifecycleListener)) {
            mLifeCycleEventListeners.remove(lifecycleListener);
        }
    }

    private LifecycleEvent getLifecycleEventFor(GestureDetector detector) {
        for (Map.Entry<LifecycleEvent, GestureDetector> entry : mDetectors.entrySet()) {
            if (entry.getValue().equals(detector)) {
                return entry.getKey();
            }
        }

        return null; //WOOOHOOO
    }

    @Override
    public void onGestureDetected(GestureDetector gestureDetector) {
        for (ILifecycleListener gestureListener : mLifeCycleEventListeners) {
            switch (getLifecycleEventFor(gestureDetector)) {
                case CONNECT:
                    gestureListener.onConnect();
                    break;
                case SELECT:
                    gestureListener.onSelect();
                    break;
                case TRANSFER:
                    gestureListener.onTransfer();
                    break;
                case DISCONNECT:
                    gestureListener.onDisconnect();
                    break;
                default:
                    break;
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


