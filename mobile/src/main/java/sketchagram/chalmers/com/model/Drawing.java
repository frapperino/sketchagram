package sketchagram.chalmers.com.model;

import android.view.MotionEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Holds the properties of a graphical drawing in order to allow it to be redrawn, at will.
 * Created by Alexander on 2015-03-26.
 */
public class Drawing {
    List<DrawingEvent> events;
    public Drawing() {
        events = new LinkedList<>();
    }
    public List<DrawingEvent> getMotions() {
        return events;
    }

    /**
     * Adds a DrawingEvent to the queue by converting the MotionEvent.
     * @param event
     */
    public void addMotion(MotionEvent event) {
        events.add(new DrawingEvent(System.nanoTime(), event));
    }

    @Override
    public String toString(){
        return "Drawing";
    }
}
