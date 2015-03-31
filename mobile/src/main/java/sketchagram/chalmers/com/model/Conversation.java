package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation {
    private List<ClientMessage> history;
    private List<ADigitalPerson> otherParticipants;

    public Conversation(List<ADigitalPerson> participants){
        history = new ArrayList<ClientMessage>();
        this.otherParticipants = participants;
    }

    public List<ClientMessage> getHistory() {
        return history;
    }

    public List<ADigitalPerson> getParticipants() {
        return otherParticipants;
    }

    public void addMessage(ClientMessage clientMessage) {
        history.add(clientMessage);
    }

    @Override
    public String toString(){
        String participants = getParticipants().toString();
        participants = participants.substring(1, participants.length()-1); //Remove [].
        return participants;
    }
}
