package sketchagram.chalmers.com.model;

import java.util.List;
import java.util.Observer;

import sketchagram.chalmers.com.network.Connection;
import sketchagram.chalmers.com.network.NetworkException;
import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Handles requests regarding the user.
 * Singleton class to make sure there can't be simultaneous instances handling requests.
 * Created by Alexander on 2015-04-23.
 */
public class UserManager implements IUserManager  {
    private User user;
    private static UserManager myInstance;

    public static IUserManager getInstance() {
        if(myInstance == null) {
            myInstance = new UserManager();
        }
        return myInstance;
    }

    private UserManager(){
        super();

    }

    @Override
    public void sendMessage(List<Contact> receiver, Object content) {
        MessageType type = null;
        if(content instanceof Drawing) {
            type = MessageType.DRAWING;
        } else if(content instanceof String) {
            type = MessageType.TEXTMESSAGE;
        } else if(content instanceof EmoticonType) {
            type = MessageType.EMOTICON;
        } else {
            throw new UnsupportedOperationException("Sending a message of type " + content.getClass() + " not supported!");
        }
        ClientMessage clientMessage = new ClientMessage(user, receiver, content, type);
        //Check whether to send message or not.
        //Depending on if message should be kept internal, i.e. user sent to self.
        if(receiver.size() == 1 && isUser(receiver.get(0))) {
            user.addMessage(clientMessage, false);
        } else {
            user.addMessage(clientMessage, true);
        }
    }

    @Override
    public boolean login(String username, String password) {
        if(Connection.getInstance().login(username,password)){
            for ( Contact user : Connection.getInstance().getContacts()){
                boolean exists = false;
                for(Contact contact : MyApplication.getInstance().getDatabase().getAllContacts()){
                    if(contact.getUsername().equals(user.getUsername())){
                        exists = true;
                        break;
                    }
                }
                if(!exists) {
                    MyApplication.getInstance().getDatabase().insertContact(user);
                }
            }
            user = new User(username, new Profile());
            Connection.getInstance().updateUsers();
            return true;
        }
        return false;
    }

    @Override
    public void createAccount(String username, String password) throws NetworkException.UsernameAlreadyTakenException {
        Connection.getInstance().createAccount(username, password);
    }

    @Override
    public void logout() {
        checkUserLoggedIn();
        Connection.getInstance().logout();
        user = null;
    }

    @Override
    public List<Contact> getAllContacts() {
        checkUserLoggedIn();
        return user.getContactList();
    }

    @Override
    public List<Conversation> getAllConversations() {
        return user.getConversationList();
    }

    @Override
    public Conversation getConversation(int conversationId) {
        return user.getConversation(conversationId);
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void addUserObserver(Observer observer) {
        user.addObserver(observer);
    }

    @Override
    public boolean addContact(String username) {
        return user.addContact(username);
    }

    @Override
    public boolean isUser(ADigitalPerson person) {
        return person.equals(user);
    }

    @Override
    public Conversation addReceivedMessage(ClientMessage clientMessage) {
        return user.addMessage(clientMessage, false);
    }

    @Override
    public boolean removeContact(Contact contact) {
        return user.removeContact(contact);
    }

    @Override
    public void updateStatuses() {
        if(user != null) {
            user.updateStatuses();
        }
    }

    private void checkUserLoggedIn() {
        if(user == null)
            throw new IllegalStateException("A user must be logged in to be able to perform this operation");
    }

    @Override
    public boolean isLoggedIn() {
        return user != null ? true : false;
    }
}
