package sketchagram.chalmers.com.model;


import sketchagram.chalmers.com.sketchagram.R;

/**
 * Created by Olliver on 2015-04-14.
 */
public enum EmoticonType {
    SAD("SAD", R.drawable.emoji_sad), HAPPY("HAPPY", R.drawable.emoji_happy),
    FLIRT("FLIRT", R.drawable.emoji_flirt), HEART("HEART", R.drawable.emoji_heart),
    THUMBSUP("THUMBSUP", R.drawable.happyface), THUMBSDOWN("THUMBSDOWN", R.drawable.happyface);
    //TODO: Change thumbs up and down to real images.

    private final String type;
    private final int drawable;

    EmoticonType(String type, int drawable){
        this.type = type;
        this.drawable = drawable;
    }

    public String toString(){
        return type;
    }

    public int getDrawable() {
        return drawable;
    }
}
