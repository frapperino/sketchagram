package sketchagram.chalmers.com.sketchagram;

import android.util.Log;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

/**
 * Created by Bosch on 12/03/15.
 */
public class ContactSync {
    private ArrayList<String> contacts;

    public ContactSync() {
        contacts = new ArrayList<>();
    }

    /**
     * Decode contacts from the phone.
     * @param data
     */
    public ContactSync(DataMap data) {
        contacts = new ArrayList<>();
        try {
            contacts = data.getStringArrayList("CONTACTS");
        } catch (NullPointerException e) {
            Log.e("CONTACTLIST", "No contacts found");
        }
    }

    /**
     *
     * @return the contacts plus a "send" alternative.
     */
    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void addContact(String name) {
        contacts.add(name);
    }

    /**
     * Puts all the contacts into the following datamap.
     * @param data
     * @return
     */
    public DataMap putToDataMap(DataMap data) {
        data.remove("RECEIVERS");
        data.putStringArrayList("RECEIVERS", contacts);
        return data;
    }
}