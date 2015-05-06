package sketchagram.chalmers.com.sketchagram;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
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
public class Drawing extends AMessage{
    List<DrawingEvent> events;
    private final int COLOR = Color.MAGENTA;

    private final Resources res;

    private byte[] staticDrawing;

    public Drawing() {
        events = new LinkedList<>();
        res = null;
    }

    public Drawing(float[] xFloat, float[] yFloat, long[] longTimes, String[] actionsString, byte[] staticDrawing, Resources res) {

        this.res = res;

        events = new LinkedList<>();

        this.setStaticDrawing(staticDrawing);

        float[] yf = yFloat;
        float[] xf = xFloat;
        long[] times = longTimes;
        String[] actions = actionsString;

        int i = 0;
        for(float f : yf) {
            events.add(new DrawingEvent(times[i], xf[i], f, DrawMotionEvents.valueOf(actions[i])));
        }

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


    public DataMap putToDataMap(DataMap data) {
        data.remove("y-coordinates");
        data.remove("x-coordinates");
        data.remove("drawing-times");
        data.remove("actions");

        long[] times = new long[events.size()];
        float[] xf = new float[events.size()];
        float[] yf = new float[events.size()];
        String[] actions = new String[events.size()];

        int i = 0;
        for(DrawingEvent de : events) {
            xf[i] = de.getX();
            yf[i] = de.getY();
            times[i] = de.getTime();
            actions[i] = de.getAction().toString();
            i++;
        }

        data.putFloatArray("x-coordinates", xf);
        data.putFloatArray("y-coordinates", yf);
        data.putLongArray("drawing-times", times);
        data.putStringArray("actions", actions);
        data.putByteArray("staticDrawing", staticDrawing);
        return data;
    }


    public void setStaticDrawing(byte[] staticDrawing) {
        this.staticDrawing = staticDrawing;
    }

    public Drawable getDrawable(){
        return new BitmapDrawable(res, getStaticDrawing());
    }

    public Bitmap getStaticDrawing() {
        return BitmapFactory.decodeByteArray(staticDrawing, 0, staticDrawing.length);
    }
}
