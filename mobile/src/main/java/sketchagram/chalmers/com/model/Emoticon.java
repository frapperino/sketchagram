package sketchagram.chalmers.com.model;

/**
 * Created by Alexander on 2015-05-02.
 */
public class Emoticon {
    private final EmoticonType emoticonType;

    public Emoticon(EmoticonType emoticonType) {
        this.emoticonType = emoticonType;
    }

    public EmoticonType getEmoticonType() {
        return emoticonType;
    }
}
