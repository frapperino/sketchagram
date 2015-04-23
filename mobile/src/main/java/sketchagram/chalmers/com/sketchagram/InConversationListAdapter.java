package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import sketchagram.chalmers.com.model.ClientMessage;

/**
 * Created by Bosch on 24/02/15.
 */
public class InConversationListAdapter extends ArrayAdapter<ClientMessage> {
    private Context context;

    public InConversationListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ClientMessage item = getItem(position);
        View viewToUse;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            viewToUse = mInflater.inflate(R.layout.inconversation_list_item, null);
            holder = new ViewHolder();
            holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }
        holder.titleText.setText(item.toString());
        if (item.getSender().getUsername().equals(MyApplication.getInstance().getUser().getUsername())) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultDate = new Date(item.getTimestamp());
            holder.titleText.setText("[" + sdf.format(resultDate) + "] Me: " + item.getContent().toString());
        }
        item.setRead(true); //Mark message as read.
        return viewToUse;
    }
}


