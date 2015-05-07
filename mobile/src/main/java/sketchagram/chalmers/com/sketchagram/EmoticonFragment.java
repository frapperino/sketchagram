package sketchagram.chalmers.com.sketchagram;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Emoticon;
import sketchagram.chalmers.com.model.EmoticonType;
import sketchagram.chalmers.com.model.UserManager;

/**
 * Displays a list of emoticons above the drawing fragment.
 * Use the {@link EmoticonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmoticonFragment extends Fragment implements  AbsListView.OnItemClickListener {
    private ListAdapter mAdapter;

    private ListView listView;

    private List<Contact> receivers;

    private final EmoticonType[] EMOTICON_TYPES = EmoticonType.values();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment EmoticonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmoticonFragment newInstance(List<Contact> receivers) {
        EmoticonFragment fragment = new EmoticonFragment();
        fragment.receivers = receivers;
        return fragment;
    }

    public EmoticonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emoticon, container, false);

        listView = (ListView) view.findViewById(R.id.emoticonList);
        listView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        UserManager.getInstance().sendMessage(receivers, new Emoticon(EMOTICON_TYPES[position]));
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_frame, new ConversationFragment())
                .addToBackStack(null).commit();
    }

    private class MyAdapter extends BaseAdapter {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            for(int i=0; i< EMOTICON_TYPES.length; i++) {
                items.add(new Item(new Emoticon(EMOTICON_TYPES[i])));
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = inflater.inflate(R.layout.fragment_emoticon_list_item, viewGroup, false);
            }
            ImageView emoticonImage = (ImageView)view.findViewById(R.id.emoticonButton);
            emoticonImage.setImageResource(((Item)getItem(i)).emoticon.getEmoticonType().getDrawable());
            return view;
        }

        private class Item {
            final private Emoticon emoticon;

            Item(Emoticon emoticon) {
                this.emoticon = emoticon;
            }

            public Emoticon getEmoticon() {
                return emoticon;
            }
        }
    }
}
