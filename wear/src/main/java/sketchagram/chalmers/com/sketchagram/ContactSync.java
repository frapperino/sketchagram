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

    public ContactSync(DataMap data) {
        contacts = new ArrayList<>();
        try {
            contacts = data.getStringArrayList("CONTACTS");
        } catch (NullPointerException e) {
            Log.e("CONTACTLIST", "No contacts found");
        }
    }

    public ArrayList<String> getContactChoices() {
        ArrayList<String> choices = contacts;
        choices.add("  Send  ");
        choices.add("Send one to all");
        return choices;
    }

    public DataMap putToDataMap(DataMap data) {
        data.remove("RECEIVERS");
        data.putStringArrayList("RECEIVERS", contacts);
        return data;
    }
}