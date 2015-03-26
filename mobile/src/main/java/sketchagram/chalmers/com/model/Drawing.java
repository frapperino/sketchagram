package sketchagram.chalmers.com.model;

import android.graphics.Path;

/**
 * Holds the properties of a graphical drawing in order to allow it to be redrawn, at will.
 * Created by Alexander on 2015-03-26.
 */
public class Drawing {
    Path path;
    public Drawing(Path path) {
        this.path = path;
    }
    public Path getPath() {
        return path;
    }
}
