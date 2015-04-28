package sketchagram.chalmers.com.model;

import java.util.List;
import java.util.Observer;

/**
 * Defines possible interactions that the user can do, as a facade towards the GUI.
 * Created by Alexander on 2015-04-23.
 */
public interface IUserManager {
    /**
     * Sends a message containing a drawing to the specified user.
     * @param receiver all contacts who are going to receive the message
     * @param content message content
     * @return true if message was sent. false otherwise.
     */
    public void sendMessage(List<Contact> receiver, Object content);

    /**
     * Attempt to login the user.
     * @param username the wanted user's username
     * @param password the corresponding password of the username
     * @return true if successful, false otherwise.
     */
    public boolean login(String username, String password);

    /**
     * Creates a new user account at the server.
     * @param username the wanted username
     * @param password the wanted password
     */
    public void createAccount(String username, String password) throws Exception;

    /**
     * Logout the user.
     */
    public void logout();

    /**
     * Returns all contacts for the
     */
    public List<Contact> getAllContacts();

    /**
     * Get all of the user's contacts.
     * @return
     */
    public List<Conversation> getAllConversations();

    /**
     * Get all of the user's conversations.
     * @param conversationId
     * @return
     */
    public Conversation getConversation(int conversationId);

    public String getUsername();

    /**
     * Set observer on User.
     * @param observer the observer.
     */
    public void addUserObserver(Observer observer);

    /**
     * Attempt to add a contact.
     * @param username username of the contact one wishes to attempt to add.
     * @return true if contact was successfully added. false otherwise.
     */
    public boolean addContact(String username);

    /**
     * Check if the person corresponds to the user.
     * @param person to test with.
     * @return true if it's the user. false otherwise.
     */
    public boolean isUser(ADigitalPerson person);

    /**
     * Call this method to add a received message.
     * @return conversation that it was appended to.
     */
    public Conversation addReceivedMessage(ClientMessage clientMessage);

    /**
     * Attempt to remove a contact.
     * @return true if successful. false otherwise.
     */
    public boolean removeContact(Contact contact);

    public boolean isLoggedIn();
}
