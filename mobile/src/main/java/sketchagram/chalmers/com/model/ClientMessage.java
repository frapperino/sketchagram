package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public class ClientMessage<T> {
    private final long timestamp;
    private final ADigitalPerson sender;
    private final List<ADigitalPerson> receivers = new ArrayList<>();
    private final T content;
    private final MessageType type;
    private boolean read;

    public ClientMessage(long timestamp, ADigitalPerson sender, List<ADigitalPerson> receiver, T content, MessageType type) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.receivers.addAll(receiver);
        this.content = content;
        this.type = type;
        this.read = false;
    }

    public T getContent(){
        return content;
    }

    public MessageType getType(){
        return type;
    }

    public ADigitalPerson getSender(){
        return sender;
    }
    public List<ADigitalPerson> getReceivers(){return receivers;}
    public long getTimestamp(){return timestamp;}
    @Override
    public String toString(){
        return sender.getUsername() + ": " + content.toString();
    }
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
