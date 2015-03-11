package sketchagram.chalmers.com.model;

import android.graphics.drawable.Drawable;

import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public class Emoticon extends AMessage {


    public Emoticon(long timestamp, ADigitalPerson sender, Set<ADigitalPerson> receiver) {
        super(timestamp, sender, receiver);
    }

    @Override
    public String getMessage() {
        return ":D";
    }

}
