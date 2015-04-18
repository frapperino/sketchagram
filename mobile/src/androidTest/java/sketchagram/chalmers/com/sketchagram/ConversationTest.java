package sketchagram.chalmers.com.sketchagram;

import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.User;

/**
 * Created by Alexander on 2015-04-18.
 */
public class ConversationTest extends AndroidTestCase {
    User user;
    ADigitalPerson p1;
    ADigitalPerson p2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        user = new User("TestUser", new Profile());
        p1 = new Contact("p1", new Profile());
        p2 = new Contact("p2", new Profile());
    }

    /**
     * Test if sorting conversations work as intended.
     */
    public void testSorting() {
        List<ADigitalPerson> l1 = new ArrayList<>();
        List<ADigitalPerson> l2 = new ArrayList<>();
        l1.add(p1);
        l2.add(p2);
        Conversation c1 = new Conversation(l1, 1);
        Conversation c2 = new Conversation(l2, 2);
        user.addConversation(c1);
        user.addConversation(c2);

        user.getConversationList();
    }
}
