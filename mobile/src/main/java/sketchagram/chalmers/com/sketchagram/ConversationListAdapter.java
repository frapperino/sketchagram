package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.DrawingEvent;
import sketchagram.chalmers.com.model.MessageType;

/**
 * Created by Alexander on 2015-04-11.
 */
public class ConversationListAdapter extends ArrayAdapter<Conversation> {
    private Context mContext;

    public ConversationListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.mContext = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Conversation item = (Conversation)getItem(position);
        View viewToUse = null;

        List<ClientMessage> messages = item.getHistory();

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            viewToUse = mInflater.inflate(R.layout.conversation_list_item, null);
            ImageView imageView = (ImageView) viewToUse.findViewById(R.id.drawing_holder);
            TextView senderView = (TextView) viewToUse.findViewById(R.id.sender);
            holder = new ViewHolder();
            holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
            viewToUse.setTag(holder);
            if (messages.size() > 0) {
                ClientMessage clientMessage = messages.get(messages.size() - 1);
                if (clientMessage.getType() == MessageType.DRAWING) {
                    Drawing drawing = ((Drawing)clientMessage.getContent());
                    if(drawing != null){
                        //TODO: make it fit into imageview.
                        imageView.setImageBitmap(drawing.getStaticDrawing());
                    }
                }
            }
            senderView.setText(item.toString());
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }
        return viewToUse;
    }
}
