package sketchagram.chalmers.com.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds the properties of a graphical drawing in order to allow it to be redrawn, at will.
 * Created by Alexander on 2015-03-26.
 */
public class Drawing {
    List<DrawingEvent> events = new LinkedList<>();
    //Color used in the drawing. TODO: Change from static to dynamic.
    private final int COLOR = Color.MAGENTA;

    private byte[] staticDrawing;

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

    public int getCOLOR() {
        return COLOR;
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

    public void setStaticDrawing(byte[] staticDrawing) {
        this.staticDrawing = staticDrawing;
    }

    public Bitmap getStaticDrawing() {
        return BitmapFactory.decodeByteArray(staticDrawing, 0, staticDrawing.length);
    }
}
