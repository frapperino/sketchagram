package sketchagram.chalmers.com.sketchagram;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Bosch on 24/04/15.
 */
public class Emoticon extends AMessage {
    private final int resid;

    Emoticon(int resid){
        this.resid = resid;
    }

    @Override
    public Bitmap getStaticDrawing() {
        return BitmapFactory.decodeResource(Resources.getSystem(), resid);
    }
}
