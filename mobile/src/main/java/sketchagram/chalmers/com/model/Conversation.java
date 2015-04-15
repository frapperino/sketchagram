package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation {
    private List<ClientMessage> history;
    private List<ADigitalPerson> participants;
    private int conversationId;

    public Conversation(List<ADigitalPerson> participants, int conversationId){
        history = new ArrayList<ClientMessage>();
        this.participants = participants;
        this.conversationId = conversationId;
    }
    public Conversation(List<ADigitalPerson> participants, List<ClientMessage> oldMessages, int conversationId){
        history = oldMessages;
        this.participants = participants;
        this.conversationId = conversationId;
    }

    public List<ClientMessage> getHistory() {
        return history;
    }

    public int getConversationId(){ return  conversationId; }

    public List<ADigitalPerson> getParticipants() {
        return participants;
    }

    public void addMessage(ClientMessage clientMessage) {
        history.add(clientMessage);
    }

    /**
     * Determines whether or not this conversation has unread messages.
     * @return true if there exists unread messages. false otherwise.
     */
    public boolean hasUnreadMessages() {
        for(ClientMessage c: history) {
            if(!c.isRead()) {
                return true;
            }
        }
        return false;
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
