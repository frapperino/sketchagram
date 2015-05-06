package sketchagram.chalmers.com.sketchagram;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/**
 * Created by Bosch on 24/04/15.
 */
public class Emoticon extends AMessage{
    private final EmoticonType emoticonType;
    private final Resources res;

    public Emoticon(EmoticonType emoticonType, Resources res) {
        this.emoticonType = emoticonType;
        this.res = res;
    }

    public EmoticonType getEmoticonType() {
        return emoticonType;
    }

    @Override
    public Drawable getDrawable(){
        return res.getDrawable(emoticonType.getDrawable());
    }
}
