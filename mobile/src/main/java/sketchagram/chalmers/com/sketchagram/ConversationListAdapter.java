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
            ImageView imageView = (ImageView) viewToUse.findViewById(R.id.imageHolder);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            TextView senderView = (TextView) viewToUse.findViewById(R.id.sender);
            holder = new ViewHolder();
            holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
            viewToUse.setTag(holder);
            if (messages.size() > 0) {
                ClientMessage clientMessage = messages.get(messages.size() - 1);
                if (clientMessage.getType() == MessageType.DRAWING) {
                    Drawing drawing = ((Drawing)clientMessage.getContent());
                    imageView.setImageBitmap(getBitmap(drawing, imageView));
                }
            }
            senderView.setText(item.toString());
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }
        return viewToUse;
    }

    private Bitmap getBitmap(Drawing drawing, ImageView imageView) {
        int HEIGHT = 40;
        int WIDTH = 40;
        float minX = Float.MAX_VALUE;
        float maxX = 0;
        float minY = Float.MAX_VALUE;
        float maxY = 0;
        List<DrawingEvent> events = drawing.getMotions();
        for(DrawingEvent event: events) {
            if(event.getX() < minX) {
                minX = event.getX();
            }
            if(event.getX() > maxX) {
                maxX = event.getX();
            }
            if(event.getY() < minY) {
                minY = event.getY();
            }
            if(event.getY() > maxY) {
                maxY = event.getY();
            }
        }
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, conf); // this creates a MUTABLE bitmap
        for(DrawingEvent e:events) {
            bitmap.setPixel((int)(HEIGHT * e.getX()), (int)(WIDTH * e.getY()), drawing.getCOLOR());
        }
        int startX = (minX == 0) ? 0 : (int)(minX*WIDTH);
        int endX = (maxX > 1) ? WIDTH : (int)(maxX*WIDTH);
        int startY = (minY == 0) ? 0 : (int)(minY*HEIGHT);
        int endY = (maxY > 1) ? HEIGHT : (int)(maxY*HEIGHT);
        //Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, startX, startY, endX, endY);
        return Bitmap.createScaledBitmap(bitmap, 400, 400, true);
    }
}
