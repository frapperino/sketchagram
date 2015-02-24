package sketchagram.chalmers.com.model.test;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.SystemUser;
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

        List<ADigitalPerson> participants = new ArrayList<ADigitalPerson>();
        participants.add(user.getContactList().get(0));
        participants.add(user.getContactList().get(5));
        participants.add(user.getContactList().get(1));
        Conversation conversation = new Conversation(participants);
            SystemUser.getInstance().getUser().addConversation(conversation);
    }
}
