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

    public List<Drawing> getDrawings() {
        return drawings;
    }

    public Drawing getFirstDrawing() {
        Drawing temp = drawings.get(0);
        drawings.remove(0);
        return temp;
    }

    public void setDrawings(List<Drawing> drawings) {
        this.drawings = drawings;
    }

    public void resetDrawing() {
        drawing = null;
    }

    public int getDrawingsAmount(){
        if(drawings != null)
            return drawings.size();
        return 0;
    }

}
