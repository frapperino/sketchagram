package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation {
    private List<ClientMessage> history;
    private Set<ADigitalPerson> participants;
    private int conversationId;

    public Conversation(Set<ADigitalPerson> participants, int conversationId){
        history = new ArrayList<ClientMessage>();
        this.participants = participants;
        this.conversationId = conversationId;
    }
    public Conversation(Set<ADigitalPerson> participants, List<ClientMessage> oldMessages, int conversationId){
        history = oldMessages;
        this.participants = participants;
        this.conversationId = conversationId;
    }

    public List<ClientMessage> getHistory() {
        return history;
    }

    public int getConversationId(){ return  conversationId; }

    public Set<ADigitalPerson> getParticipants() {
        return participants;
    }

    public void addMessage(ClientMessage clientMessage) {
        history.add(clientMessage);
    }

    @Override
    public String toString(){
        getParticipants().remove(SystemUser.getInstance().getUser());// to not display user
        String participants = getParticipants().toString();
        participants = participants.substring(1, participants.length()-1); //Remove [].
        getParticipants().add(SystemUser.getInstance().getUser());//to keep list consistent
        return participants;
    }
}
