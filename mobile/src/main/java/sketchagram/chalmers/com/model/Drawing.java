package sketchagram.chalmers.com.model;

import android.view.MotionEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Holds the properties of a graphical drawing in order to allow it to be redrawn, at will.
 * Created by Alexander on 2015-03-26.
 */
public class Drawing {
    List<MotionEvent> events;
    public Drawing() {
        events = new LinkedList<MotionEvent>();
    }
    public List<MotionEvent> getMotions() {
        return events;
    }
    public void addMotion(MotionEvent event) {
        events.add(event);
    }
}
