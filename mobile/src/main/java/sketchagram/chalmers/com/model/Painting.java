package sketchagram.chalmers.com.model;

import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public class Painting extends AMessage {

    protected Painting(long timestamp, ADigitalPerson sender, Set<ADigitalPerson> receiver ) {
        super(timestamp, sender, receiver);
    }

    @Override
    public <T> T getMessage() {
        return null;
    }
}
