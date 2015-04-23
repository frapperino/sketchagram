package sketchagram.chalmers.com.sketchagram;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.Contact;

/**
 * Created by Bosch on 12/03/15.
 */
public class ContactsSync {
    private List<Contact> contacts;

    public ContactsSync(List<Contact> contactList) {
        this.contacts = contactList;
    }

    public ContactsSync(DataMap data) {
        contacts = new ArrayList<>();
        for(String name : data.getStringArrayList("RECEIVERS")){
            for(Contact c : MyApplication.getInstance().getUser().getContactList())
                if(name.equals(c.getUsername()))
                    contacts.add(c);
        }
    }

    public List<Contact> getContacts(){
        return contacts;
    }

    public DataMap putToDataMap(DataMap dataMap) {
        ArrayList<String> ls = new ArrayList<>();
        for(Contact c : contacts)
            ls.add(c.getUsername());
        dataMap.remove("CONTACTS");
        dataMap.putStringArrayList("CONTACTS", ls);
        return dataMap;
    }
}