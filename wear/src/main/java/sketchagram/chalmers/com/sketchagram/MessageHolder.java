package sketchagram.chalmers.com.sketchagram;

import android.graphics.BitmapFactory;

import java.util.List;

/**
 * Created by Bosch on 02/04/15.
 */
public class MessageHolder {

    private static MessageHolder instance = null;
    private static List<AMessage> messages;

    protected MessageHolder() {}

    public static MessageHolder getInstance() {
        if(instance == null)
            instance = new MessageHolder();

        return instance;

    }


    public AMessage getMessage(int i) {
        return messages.get(i);
    }

    public void addMessage(String emoji, int pos){
        messages.add(pos, new Emoticon(EmoticonType.valueOf(emoji).getRes()));
    }

    public void setDrawings(List<AMessage> messages) {
        this.messages = messages;
    }

    public int getDrawingsAmount(){
        return messages != null ? messages.size() : 0;
    }

}
