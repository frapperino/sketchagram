package sketchagram.chalmers.com.model;

import java.util.List;

/**
 * Created by Bosch on 10/02/15.
 */
public class RealtimePainting extends Painting {

    protected RealtimePainting(long timestamp, ADigitalPerson sender, List<ADigitalPerson> receiver ) {
        super(timestamp, sender, receiver);
    }
}
