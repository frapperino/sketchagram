package sketchagram.chalmers.com.sketchagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sketchagram.chalmers.com.model.Contact;

/**
 * Created by Bosch on 20/02/15.
 */
public class ContactAdapter extends ArrayAdapter<Contact>{

    public ContactAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.fragment_item_list, null);

        }

        Contact c = getItem(position);

        return v;

    }
}
