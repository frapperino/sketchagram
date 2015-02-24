package sketchagram.chalmers.com.model;

import java.util.List;

/**
 * Created by Bosch on 10/02/15.
 */
public class TextMessage extends AMessage {

    protected TextMessage(long timestamp, ADigitalPerson sender, List<ADigitalPerson> receiver) {
        super(timestamp, sender, receiver);
    }

    @Override
    public <T> T getMessage() {
        return null;
    }
}
