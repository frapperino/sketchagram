package sketchagram.chalmers.com.sketchagram;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.UserManager;

/**
 * Created by Bosch on 12/03/15.
 */
public class ContactSync {
    private List<Contact> contacts;

    public ContactSync(List<Contact> contactList) {
        this.contacts = contactList;
    }


    /**
     * Decode contacts from a datamap.
     * @param data
     */
    public ContactSync(DataMap data) {
        contacts = new ArrayList<>();
        for(String name : data.getStringArrayList("RECEIVERS")){
            for(Contact c : UserManager.getInstance().getAllContacts())
                if(name.equals(c.getUsername()))
                    contacts.add(c);
        }
    }

    public List<Contact> getContacts(){
        return contacts;
    }


    /**
     * Puts all the contacts into the following datamap.
     * @param dataMap
     * @return
     */
    public DataMap putToDataMap(DataMap dataMap) {
        ArrayList<String> ls = new ArrayList<>();
        for(Contact c : contacts)
            ls.add(c.getUsername());
        dataMap.remove("CONTACTS");
        dataMap.putStringArrayList("CONTACTS", ls);
        return dataMap;
    }
}