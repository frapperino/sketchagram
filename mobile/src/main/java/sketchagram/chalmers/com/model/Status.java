package sketchagram.chalmers.com.model;

/**
 * Created by Olliver on 2015-04-12.
 */
public enum Status {
    ONLINE("ONLINE"), OFFLINE("OFFLINE"), AWAY("AWAY");

    private final String type;

    Status(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }
}
