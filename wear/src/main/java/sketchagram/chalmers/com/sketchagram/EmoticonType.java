package sketchagram.chalmers.com.sketchagram;

/**
 * Created by Bosch on 24/04/15.
 */
public enum EmoticonType {
    SAD("SAD", R.drawable.emoji_sad), HAPPY("HAPPY", R.drawable.emoji_happy),
    FLIRT("FLIRT", R.drawable.emoji_flirt), HEART("HEART", R.drawable.emoji_heart),
    THUMBSUP("THUMBSUP", R.drawable.ic_thumbs_up_red), THUMBSDOWN("THUMBSDOWN", R.drawable.ic_thumbs_down_red);


    private final String type;
    private final int res;

    EmoticonType(String type, int res){
        this.type = type;
        this.res = res;
    }

    public String toString(){
        return type;
    }

    public int getRes(){
        return res;
    }
}
