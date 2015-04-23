package sketchagram.chalmers.com.sketchagram;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.User;

/**
 * Does several tests for consistency in the user.
 * Requires the server to be active though, else it will fail.
 * Created by Alexander on 2015-04-23.
 */
public class UserTest extends BasicSetupTest {
    private MyApplication myApplication;
    private User user;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myApplication = MyApplication.getInstance();
    }

    /**
     * Test logging in and make sure that it's the correct user.
     */
    public void testLogin() {
        myApplication.login(TEST_USERNAME, TEST_PASSWORD);
        assertEquals(TEST_USERNAME, myApplication.getUser().getUsername());
    }

    /**
     * Test if sending a message to oneself actually works.
     */
    public void testSendingMessageToSelf() {
        List<ADigitalPerson> receivers = new ArrayList();
        receivers.add(user);
        ClientMessage clientMessage = new ClientMessage(user, receivers, "TEST", MessageType.TEXTMESSAGE);
        user.addMessage(clientMessage, true);
        assertTrue(user.getConversationList().get(0).getHistory().get(0) == clientMessage);
    }
}
