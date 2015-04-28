package sketchagram.chalmers.com.sketchagram;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.IUserManager;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.User;
import sketchagram.chalmers.com.model.UserManager;

/**
 * Does several tests for consistency in the user.
 * Requires the server to be active though, else it will fail.
 * Created by Alexander on 2015-04-23.
 */
public class UserTest extends BasicSetupTest {
    private IUserManager userManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userManager = UserManager.getInstance();
    }

    /**
     * Test logging in and make sure that it's the correct user.
     */
    public void testLogin() {
        userManager.login(TEST_USERNAME, TEST_PASSWORD);
        assertEquals(TEST_USERNAME, userManager.getUsername());
    }

    /**
     * Test if sending a message to oneself actually works.
     */
    public void testSendingMessageToSelf() {
        Contact contact = new Contact(TEST_USERNAME, new Profile());
        List<Contact> receivers = new ArrayList();
        receivers.add(contact);
        userManager.sendMessage(receivers, "Hello");
        assertEquals(TEST_USERNAME, userManager.getAllConversations().get(0).getHistory().get(0).getReceivers().get(0).toString());
    }
}
