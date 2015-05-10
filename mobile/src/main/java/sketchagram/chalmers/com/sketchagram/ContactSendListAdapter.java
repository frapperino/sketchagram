package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.UserManager;

/**
 * Created by Bosch and Alexander Harenstam on 20/02/15.
 * Tutorial: https://www.airpair.com/android/list-fragment-android-studio
 */
public class ContactSendListAdapter extends ArrayAdapter<Contact>{
    private Context context;

    public ContactSendListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
        ImageView status_image_send;
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
        Contact item = (Contact) getItem(position);
        View viewToUse = null;
        final Contact contact = UserManager.getInstance().getAllContacts().get(position);

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            viewToUse = mInflater.inflate(R.layout.contact_send_list_item, null);
            holder = new ViewHolder();
            holder.titleText = (TextView) viewToUse.findViewById(R.id.titleTextView);
            holder.status_image_send = (ImageView) viewToUse.findViewById(R.id.status_image_send);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }
        holder.titleText.setText(item.getUsername());

        if (contact.getStatus() != null) {
            switch (contact.getStatus()) {
                case ONLINE:
                    holder.status_image_send.setBackgroundResource(R.drawable.status_online);
                    break;
                case OFFLINE:
                    holder.status_image_send.setBackgroundResource(R.drawable.status_offline);
                    break;
                case AWAY:
                    holder.status_image_send.setBackgroundResource(R.drawable.status_away);
                    break;
                default:
                    holder.status_image_send.setBackgroundColor(Color.WHITE);
                    break;
            }
        }
        return viewToUse;
    }
}
