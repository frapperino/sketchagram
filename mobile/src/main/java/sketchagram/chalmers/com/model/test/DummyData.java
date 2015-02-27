package sketchagram.chalmers.com.model.test;

import android.os.SystemClock;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.model.TextMessage;
import sketchagram.chalmers.com.model.User;

/**
 * Created by Bosch on 20/02/15.
 */

//TODO: Remove once real profiles have been created.
public class DummyData {

    public static void injectData(){
        User user = SystemUser.getInstance().getUser();

        Contact contact = new Contact("Jabbe", new Profile());
        user.addContact(contact);
        contact = new Contact("Frappe", new Profile());
        user.addContact(contact);
        contact = new Contact("Lam(m)", new Profile());
        user.addContact(contact);
        contact = new Contact("Alex", new Profile());
        user.addContact(contact);
        contact = new Contact("Olliver", new Profile());
        user.addContact(contact);
        contact = new Contact("Bosch", new Profile());
        user.addContact(contact);
        contact = new Contact("abc123", new Profile());
        user.addContact(contact);

        List<ADigitalPerson> participants = new ArrayList<ADigitalPerson>();
        participants.add(user.getContactList().get(0));
        participants.add(user.getContactList().get(5));
        participants.add(user.getContactList().get(1));
        Conversation conversation = new Conversation(participants);

        participants.remove(1);
        TextMessage text = new TextMessage(SystemClock.currentThreadTimeMillis(), user.getContactList().get(5), participants);
        text.setTextMessage("Yolo");
        conversation.addMessage(text);

        participants.add(user.getContactList().get(5));
        participants.remove(1);
        text = new TextMessage(SystemClock.currentThreadTimeMillis(), user.getContactList().get(1), participants);
        text.setTextMessage("Haha");

        conversation.addMessage(text);
        participants.add(user.getContactList().get(1));
        participants.remove(0);
        text = new TextMessage(SystemClock.currentThreadTimeMillis(), user.getContactList().get(0), participants);
        text.setTextMessage("Kul man kan ha då...");
        conversation.addMessage(text);

        SystemUser.getInstance().getUser().addConversation(conversation);
    }
}
