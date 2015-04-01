package sketchagram.chalmers.com.model;

import android.view.MotionEvent;

/**
 * Represents a user interaction while drawing.
 * Defines the type of input and time it was made.
 * Created by Alexander on 2015-03-28.
 */
public class DrawingEvent {
    private float x;
    private float y;
    private DrawMotionEvents action;
    private MotionEvent motionEvent;
    private long time;

    public DrawingEvent(long time, float x, float y, DrawMotionEvents action) {
        this.time = time;
        this.x = x;
        this.y = y;
        this.action = action;
    }

    public DrawMotionEvents getAction(){ return this.action; }

    public float getX() {return this.x; }

    public float getY() {return this.y; }

    public long getTime() {
        return time;
    }
}
