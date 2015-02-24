package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import sketchagram.chalmers.com.model.AMessage;
import sketchagram.chalmers.com.model.SystemUser;

/**
 * Created by Bosch on 24/02/15.
 */
public class InConversationListAdapter extends ArrayAdapter<AMessage> {
    private Context context;
    private boolean useList = true;

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
        ViewHolder holder = null;
        AMessage item = (AMessage)getItem(position);
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            if(useList){
                viewToUse = mInflater.inflate(R.layout.contact_list_item, null);
            } else {
                viewToUse = mInflater.inflate(R.layout.contact_grid_item, null);
            }

            holder = new ViewHolder();
            holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.titleText.setText(item.getSENDER().getUsername() + ": " + item.getMessage().toString());
        if(item.getSENDER().getUsername().equals(SystemUser.getInstance().getUser().getUsername()))
            holder.titleText.setText("Me: " + item.getMessage().toString());

        return viewToUse;
    }
}


