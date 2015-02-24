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
        for(int i=0; i<5; i++) {
            Contact contact = new Contact("Contact"+i, new Profile());
            user.addContact(contact);
        }
        List<ADigitalPerson> particitpants = new ArrayList<ADigitalPerson>();
        particitpants.add(new Contact("Jabbe", new Profile()));
        particitpants.add(new Contact("Frappe", new Profile()));
        particitpants.add(new Contact("Lam(m)", new Profile()));
        Conversation conversation = new Conversation();
        conversation.setParticipants(particitpants);
            SystemUser.getInstance().getUser().addConversation(conversation);
    }
}
