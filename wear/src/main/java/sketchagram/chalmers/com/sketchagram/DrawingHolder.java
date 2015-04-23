package sketchagram.chalmers.com.sketchagram;

import java.util.List;

/**
 * Created by Bosch on 02/04/15.
 */
public class DrawingHolder {

    private static DrawingHolder instance = null;
    private static Drawing drawing;
    private static List<Drawing> drawings;

    protected DrawingHolder() {}

    public static DrawingHolder getInstance() {
        if(instance == null)
            instance = new DrawingHolder();

        return instance;

    }

    public Drawing getDrawing(){
        return drawing;
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
    }

    public Drawing getDrawing(int i) {
        return drawings.get(i);
    }

    public void setDrawings(List<Drawing> drawings) {
        drawings.clear();
        this.drawings = drawings;
    }

    public void resetDrawing() {
        drawing = null;
    }

    public int getDrawingsAmount(){
        return drawings != null ? drawings.size() : 0;
    }

}
