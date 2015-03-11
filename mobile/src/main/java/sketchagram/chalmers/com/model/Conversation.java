package sketchagram.chalmers.com.model;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sketchagram.chalmers.com.sketchagram.MyApplication;

/**
 * Created by Bosch on 10/02/15.
 */
public class Conversation {
    private List<AMessage> history;
    private Set<ADigitalPerson> participants;

    public Conversation(Set<ADigitalPerson> participants){
        history = new ArrayList<AMessage>();
        this.participants = participants;
    }

    public List<AMessage> getHistory() {
        return history;
    }

    public Set<ADigitalPerson> getParticipants() {
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
