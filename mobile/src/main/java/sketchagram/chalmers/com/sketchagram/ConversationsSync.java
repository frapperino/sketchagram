package sketchagram.chalmers.com.sketchagram;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.UserManager;

/**
 * Created by Bosch on 31/03/15.
 */
public class ConversationsSync {
    private List<Conversation> conversations;

    public ConversationsSync() {
        conversations = UserManager.getInstance().getAllConversations();

    }

    public List<Conversation> getConversations(){
        return conversations;
    }

    public DataMap putToDataMap(DataMap dataMap) {
        ArrayList<String> ls = new ArrayList<>();
        List<ADigitalPerson> conversationList;
        for (Conversation c : conversations) {
            conversationList = new ArrayList<>(c.getParticipants());
            ls.add(conversationList.get(0).getUsername());
        }
        dataMap.remove("CONVERSATIONS");
        dataMap.putStringArrayList("CONVERSATIONS", ls);
        return dataMap;
    }
}

