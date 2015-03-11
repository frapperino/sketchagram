package sketchagram.chalmers.com.model;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation {
    private List<AMessage> history;
    private List<ADigitalPerson> participants;

    public Conversation(List<ADigitalPerson> participants){
        history = new ArrayList<AMessage>();
        this.participants = participants;
    }

    public List<AMessage> getHistory() {
        return history;
    }

    public List<ADigitalPerson> getParticipants() {
        return participants;
    }

    public void addMessage(AMessage message) {

        history.add(message);
        Handler handler = new Handler(MyApplication.getContext().getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        handler.post(runnable);
    }

    @Override
    public String toString(){
        return getParticipants().toString();
    }
}
