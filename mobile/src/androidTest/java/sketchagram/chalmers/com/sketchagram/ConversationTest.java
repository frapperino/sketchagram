package sketchagram.chalmers.com.sketchagram;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.util.Log;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.User;

/**
 * Created by Alexander on 2015-04-18.
 */
public class ConversationTest extends TestCase {
    ADigitalPerson user;
    ADigitalPerson p1;
    ADigitalPerson p2;
    List<ADigitalPerson> userReceiver;
    List<Conversation> conversationList;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        user = new User("TestUser", new Profile());
        p1 = new Contact("p1", new Profile());
        p2 = new Contact("p2", new Profile());
        userReceiver = new ArrayList<>();
        userReceiver.add(user);
        conversationList = new ArrayList<>();
    }

    /**
     * Test if sorting conversations work as intended.
     */
    public void testSorting() {
        List<ADigitalPerson> firstParticipants = new ArrayList<>();
        firstParticipants.add(p1);
        firstParticipants.add(user);

        List<ADigitalPerson> secondParticipants = new ArrayList<>();
        secondParticipants.add(p2);
        secondParticipants.add(user);

        Conversation firstConversation = new Conversation(firstParticipants, 1);
        Conversation secondConversation = new Conversation(secondParticipants, 2);

        ClientMessage messageOne = new ClientMessage(System.currentTimeMillis() + 100000, p1, userReceiver, "Hello user from p1", MessageType.TEXTMESSAGE, true);
        ClientMessage messageTwo = new ClientMessage(System.currentTimeMillis() + 200000, p2, userReceiver, "Hello user from p2", MessageType.TEXTMESSAGE, true);

        firstConversation.addMessage(messageOne);
        secondConversation.addMessage(messageTwo);

        conversationList.add(firstConversation);
        conversationList.add(secondConversation);

        Collections.sort(conversationList);
        assertTrue(conversationList.get(0) == secondConversation);
    }
}
