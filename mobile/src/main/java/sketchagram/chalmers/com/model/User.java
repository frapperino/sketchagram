package sketchagram.chalmers.com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bosch on 10/02/15.
 */
public class User extends ADigitalPerson {
    private String password = "password";   //TODO: replace with real password.
    private boolean requireLogin = true;
    private List<Conversation> conversationList;
    private List<Contact> contactList;


    public User(String username, Profile profile) {
        super(username, profile);
        conversationList = new ArrayList<Conversation>();
        contactList = new ArrayList<Contact>();
    }

    public void addContact(Contact contact){
        if(contact == null) {
            throw new IllegalArgumentException("contact is invalid!");
        }
        contactList.add(contact);
    }

    public void addConversation(Conversation conversation){
        boolean exist = false;
        for(Conversation c : conversationList){
            if(c.getParticipants().equals(conversation.getParticipants())) {
                for (AMessage msg : conversation.getHistory())
                    c.addMessage(msg);
                exist = true;
            }

        }
        if(!exist)
            conversationList.add(conversation);
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    @Override
    public String toString() {
        return getUsername();
    }

}
