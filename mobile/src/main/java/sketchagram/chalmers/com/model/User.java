package sketchagram.chalmers.com.model;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sketchagram.chalmers.com.network.Connection;
import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * A representation of the application user's user information and its interface.
 * Created by Bosch on 10/02/15.
 */
public class User extends ADigitalPerson  {
    private List<Conversation> conversationList;
    private List<Contact> contactList;

    public User(String username, Profile profile) {
        super(username, profile);
        conversationList = MyApplication.getInstance().getDatabase().getAllConversations(username.toLowerCase());
        contactList = MyApplication.getInstance().getDatabase().getAllContacts();
        setStatuses();
    }

    /**
     * Adds a new conversation.
     *
     * @param conversation the conversation to be added.
     */
    private void addConversation(Conversation conversation){
        boolean exist = false;
        for(Conversation c : conversationList){
            if(c.getParticipants().equals(conversation.getParticipants())) {
                exist = true;
            }
        }
        if(!exist)
            conversationList.add(conversation);
        updateObservers(null);
    }

    private void setStatuses(){
        for(Contact contact : Connection.getInstance().getContacts()){
            Contact con = contactList.get(contactList.indexOf(contact));
            con.setStatus(contact.getStatus());
        }
    }

    /**
     * Gets contacts from database which is synced with server.
     * @return the contactlist
     */
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

    /**
     * Adds a contacts.
     *
     * @param userName contact to be added.
     */
    public boolean addContact(String userName){
        boolean success = Connection.getInstance().addContact(userName);
        if(success) {
            Contact newContact = new Contact(userName, new Profile());
            MyApplication.getInstance().getDatabase().insertContact(newContact);
            contactList.add(newContact);
        }
        return success;
    }

    public boolean removeContact(Contact contact){
        boolean success = Connection.getInstance().removeContact(contact.getUsername());
        if(success){
            List<ADigitalPerson> participants = new ArrayList<>();
            participants.add(contact);
            participants.add(this);
            Conversation conversation = conversationExists(participants);
            if(conversation != null) {
                MyApplication.getInstance().getDatabase().removeConversation(conversation);
                conversationList.remove(conversation);
            }
            MyApplication.getInstance().getDatabase().deleteContact(contact);
            contactList.remove(contact);
        }
        updateObservers(null);
        return success;
    }

    /**
     * Adds a message that was received from the server to the proper conversation.
     * @param clientMessage The message received.
     * @param send If the message is to be sent via the server
     * @return The conversation which the message was appended to.
     */
    public Conversation addMessage(ClientMessage clientMessage, boolean send){
        List<ADigitalPerson> participants = new ArrayList<>();
        participants.addAll(clientMessage.getReceivers());
        participants.add(clientMessage.getSender());
        Conversation conversation = null;

        int conversationId = MyApplication.getInstance().getDatabase().insertMessage(clientMessage);
        if(conversationId >= 0) {
            if(!((ADigitalPerson)clientMessage.getReceivers().get(0)).getUsername().equals(clientMessage.getSender().getUsername()) && send){
                Connection.getInstance().sendMessage(clientMessage);
            }
            conversation = getConversation(conversationId);
            if(conversation == null) {
                conversation = new Conversation(participants, conversationId);
                this.addConversation(conversation);
            }
            conversation.addMessage(clientMessage);
            sortConversations();
            updateObservers(clientMessage);
        }
        return conversation;
    }

    /**
     * Checks if the receiver list matches the specified conversation
     * @param participants
     * @return
     */
    private Conversation conversationExists(List<ADigitalPerson> participants){
        List<Conversation> convList = this.getConversationList();
        for(Conversation c : convList){
            boolean same = true;
            for(ADigitalPerson participant : c.getParticipants()) {
                boolean participantexists = false;
                for(ADigitalPerson receiver : participants){
                    if(participant.equals(receiver)){
                        participantexists = true;
                        break;
                    }
                }
                if(!participantexists){
                    same = false;
                    break;
                }
            }
            if(same){
                return c;
            }
        }
        return null;
    }

    public boolean changePassword(String password) {
        return Connection.getInstance().changePassword(password);
    }

    private void updateObservers(final ClientMessage message){
        setChanged();
        Handler handler = new Handler(MyApplication.getContext().getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                notifyObservers(message);
            }
        };
        handler.post(runnable);
    }

    /**
     * Retrieve a conversation with a requested id.
     * @param conversationId Id of the conversation.
     * @return The conversation with the corresponding id. Otherwise null.
     */
    public Conversation getConversation(int conversationId) {
        for(Conversation c: conversationList) {
            if(c.getConversationId() == conversationId) {
                return c;
            }
        }
        return null;
    }

    public List<String> search(String userName){
        return Connection.getInstance().searchUsers(userName);
    }

    /**
     * Sorts conversations when an update has come.
     */
    private void sortConversations() {
       Collections.sort(conversationList);
    }
}
