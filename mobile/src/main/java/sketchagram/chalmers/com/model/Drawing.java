package sketchagram.chalmers.com.model;

import android.view.MotionEvent;

import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds the properties of a graphical drawing in order to allow it to be redrawn, at will.
 * Created by Alexander on 2015-03-26.
 */
public class Drawing {
    List<DrawingEvent> events = new LinkedList<>();
    public Drawing() {
        events = new LinkedList<>();
    }

    public Drawing(DataMap data) {

        events = new LinkedList<>();

        float[] yf = data.getFloatArray("y-coordinates");
        float[] xf = data.getFloatArray("x-coordinates");
        long[] times = data.getLongArray("drawing-times");
        String[] actions = data.getStringArray("actions");

        for(int i = 0; i < times.length; i++)
            events.add(new DrawingEvent(times[i], xf[i], yf[i], DrawMotionEvents.valueOf(actions[i])));


    }

    public List<DrawingEvent> getMotions() {
        return events;
    }

    /**
     * Adds a DrawingEvent to the queue by converting the MotionEvent.
     * @param event
     */
    public void addMotion(DrawingEvent event) {
        events.add(event);
    }

    @Override
    public String toString(){
        return "Drawing";
    }

    public float[] getX() {
        List<Float> fs = new ArrayList<>();
        float[] xf;
        for(DrawingEvent event : events) {
            fs.add(event.getX());
        }

        xf = new float[fs.size()];
        int i = 0;
        for(float f : fs) {
            xf[i] = f;
        }
        return xf;
    }

    public float[] getY() {
        List<Float> fs = new ArrayList<>();
        float[] yf;
        for(DrawingEvent event : events) {
            fs.add(event.getY());
        }

        yf = new float[fs.size()];
        int i = 0;
        for(float f : fs) {
            yf[i] = f;
        }
        return yf;
    }

    public long[] getTimes() {
        long[] times = new long[events.size()];
        int i = 0;
        for(DrawingEvent event : events) {
            times[i] = event.getTime();
            i++;
        }
        return times;
    }

    public String[] getActions() {
        String[] actions = new String[events.size()];
        int i = 0;
        for(DrawingEvent event : events) {
            actions[i] = event.getAction().name();
        }
        return actions;
    }
}
