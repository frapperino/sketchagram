package sketchagram.chalmers.com.sketchagram;

import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds the properties of a graphical drawing in order to allow it to be redrawn, at will.
 * Created by Alexander on 2015-03-26.
 */
public class Drawing {
    List<DrawingEvent> events;
    List<Float> xs = new ArrayList<>();
    List<Float> ys = new ArrayList<>();

    public Drawing() {
        events = new LinkedList<>();
    }

    public Drawing(DataMap data) {

        float[] yf = data.getFloatArray("y-coordinates");
        for(float f : yf) {
            ys.add(f);
        }

        float[] xf = data.getFloatArray("x-coordinates");
        for(float f : xf) {
            xs.add(f);
        }



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

    public DataMap putToDataMap(DataMap data) {
        data.remove("DRAWING");
        for(DrawingEvent de : events) {
            xs.add(de.getMotionEvent().getRawX());
            ys.add(de.getMotionEvent().getRawY());
        }
        Log.e("Coords", xs.toString());
        Log.e("Coords", ys.toString());

        float[] xf = new float[xs.size()];
        int i = 0;
        for(float f : xs) {
            xf[i] = f;
            i++;
        }

        float[] yf = new float[ys.size()];
        i = 0;
        for(float f : ys) {
            yf[i] = f;
            i++;
        }

        data.putFloatArray("x-coordinates", xf);
        data.putFloatArray("y-coordinates", yf);
        return data;
    }
}
