package sketchagram.chalmers.com.model;

import android.view.MotionEvent;

/**
 * Represents a user interaction while drawing.
 * Defines the type of input and time it was made.
 * Created by Alexander on 2015-03-28.
 */
public class DrawingEvent {
    private MotionEvent motionEvent;
    private long time;

    public DrawingEvent(long time, MotionEvent event) {
        this.time = time;
        this.motionEvent = event;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    public long getTime() {
        return time;
    }
}
