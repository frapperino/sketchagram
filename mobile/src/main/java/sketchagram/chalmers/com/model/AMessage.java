package sketchagram.chalmers.com.model;

import java.util.List;

/**
 * Created by Bosch on 10/02/15.
 */
public abstract class AMessage {
    private final long TIMESTAMP;
    private final ADigitalPerson SENDER;
    private final List<ADigitalPerson> RECEIVER;

    protected AMessage(long timestamp, ADigitalPerson sender, List<ADigitalPerson> receiver) {
        this.TIMESTAMP = timestamp;
        this.SENDER = sender;
        this.RECEIVER = receiver;
    }

    public abstract  <T> T getMessage();

    public ADigitalPerson getSENDER(){
        return SENDER;
    }

}
