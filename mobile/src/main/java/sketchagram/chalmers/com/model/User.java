package sketchagram.chalmers.com.model;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sketchagram.chalmers.com.network.Connection;
import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Created by Bosch on 10/02/15.
 */
public class User extends ADigitalPerson  {
    private String password = "password";   //TODO: replace with real password.
    private boolean requireLogin = true;
    private List<Conversation> conversationList;
    private List<Contact> contactList;


    public User(String username, Profile profile) {
        super(username, profile);
        conversationList = MyApplication.getInstance().getDatabase().getAllConversations(username);
        /*for(Contact contact : getContactList()) {
            List<ClientMessage> messageList = MyApplication.getInstance().getDatabase().getAllMessagesFromAContact(contact);
            if(!messageList.isEmpty()) {
                List<ADigitalPerson> participants = new ArrayList<>();
                participants.add(contact);
                conversationList.add(new Conversation(participants, messageList));
            }
        }*/
        contactList = MyApplication.getInstance().getDatabase().getAllContacts();
    }

    /**
     * Adds a new conversation.
     *
     * @param conversation the conversation to be added.
     */
    public void addConversation(Conversation conversation){
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
            participants.add(SystemUser.getInstance().getUser());
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
     * Sends the specified message
     * @param clientMessage The message to be sent contains receivers
     */
    public void sendMessage(ClientMessage clientMessage){
        boolean exist = true;
        Conversation conversation = null;
        List<ADigitalPerson> participants = new ArrayList<>();
        participants.addAll(clientMessage.getReceivers());
        participants.add(clientMessage.getSender());

        conversation = conversationExists(participants);
        /*if(conversation == null){
            exist = false;
        }*/

        int conversationId = MyApplication.getInstance().getDatabase().insertMessage(clientMessage);
        if(conversationId >= 0) {
            if(!((ADigitalPerson)clientMessage.getReceivers().get(0)).getUsername().equals(clientMessage.getSender().getUsername())){
                Connection.getInstance().sendMessage(clientMessage);
            }
            /*if(!exist) {
                conversation = new Conversation(participants, conversationId);
                this.addConversation(conversation);
            }
            conversation.addMessage(clientMessage);*/
            addMessage(clientMessage);
            updateObservers(clientMessage);
        }
    }

    /**
     * Adds a message that was received from the server to the proper conversation.
     * @param clientMessage The message received.
     * @return The conversation which the message was appended to.
     */
    public Conversation addMessage(ClientMessage clientMessage){
        Conversation conversation;
        List<ADigitalPerson> participants = new ArrayList<>();
        participants.addAll(clientMessage.getReceivers());
        participants.add(clientMessage.getSender());

        conversation = conversationExists(participants);
        boolean exist = true;

        if(conversation == null) {
            exist = false;
        }
        int conversationId = MyApplication.getInstance().getDatabase().insertMessage(clientMessage);
        if(conversationId >= 0) {
            if(!exist) {
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
        List<Conversation> convList = SystemUser.getInstance().getUser().getConversationList();
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
