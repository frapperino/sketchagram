package sketchagram.chalmers.com.sketchagram;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.SystemUser;

/**
 * Created by Bosch on 31/03/15.
 */
public class ConversationsSync {
    private List<Conversation> conversations;

    public ConversationsSync() {
        conversations = SystemUser.getInstance().getUser().getConversationList();

    }

    public List<Conversation> getConversations(){
        return conversations;
    }

    public DataMap putToDataMap(DataMap dataMap) {
        ArrayList<String> ls = new ArrayList<>();
        for(Conversation c : conversations)
            ls.add(c.getParticipants().get(0).getUsername());
        dataMap.remove("CONVERSATIONS");
        dataMap.putStringArrayList("CONVERSATIONS", ls);
        return dataMap;
    }
}

