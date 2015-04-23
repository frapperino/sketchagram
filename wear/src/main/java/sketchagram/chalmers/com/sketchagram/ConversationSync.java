package sketchagram.chalmers.com.sketchagram;

import android.util.Log;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

/**
 * Created by Bosch on 12/03/15.
 */
public class ConversationSync {
    private ArrayList<String> conversations;

    public ConversationSync(DataMap data) {
        try {
            conversations = data.getStringArrayList("CONVERSATIONS");
            Log.e("CONVERSATIONSSYNC", conversations.toString());
        } catch (NullPointerException e) {
            Log.e("CONVERSATIONSLIST", "No conversations found");
            conversations = new ArrayList<>();
        }
    }

    public ArrayList<String> getConversations() {
        return conversations;
    }

    public DataMap putToDataMap(DataMap data) {
        Log.e("CONVERSATIONSSYNC", conversations.toString());
        data.putStringArrayList("CONVERSATIONS", conversations);
        return data;
    }
}