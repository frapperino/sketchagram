package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.SystemUser;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ConversationFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private GridView gridView;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConversationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<Conversation>(getActivity(),
                android.R.layout.simple_list_item_1, SystemUser.getInstance().getUser().getConversationList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.conversation_list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        //Frappe
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(new MyAdapter(getActivity(), SystemUser.getInstance().getUser().getConversationList()));


        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        //gridView.setOnItemClickListener(this);

        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("ITEM_CLICK", "ITEM_CLICK");
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            String participants = SystemUser.getInstance().getUser().
                    getConversationList().get(position).getParticipants().toString();
            participants = participants.substring(1, participants.length()-1); //Remove [].
            getActivity().getSharedPreferences("Participants", 0)
                    .edit()
                    .clear()
                    .putString("Participants", participants)
                    .commit();
            mListener.onFragmentInteraction("conversation " + participants); //TODO: how to find right conversation.
        }
    }

    /**
     * Update list graphically when model has changed.
     */
    public void updateList() {
        BaseAdapter adapter = (BaseAdapter)mAdapter;
        adapter.notifyDataSetChanged();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }


    //Frappe
    private class MyAdapter extends BaseAdapter
    {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public MyAdapter(Context context, List<Conversation> conversations)
        {
            inflater = LayoutInflater.from(context);

            //TODO: hur få getName, getDrawing + timestamp från servern?
            items.add(new Item("Danny","12:54", R.drawable.danny));
            items.add(new Item("Alex","14:22", R.drawable.alex));
            items.add(new Item("Frappe","29 Mar, 2015", R.drawable.frappe));
            items.add(new Item("Jabbe","1 Feb, 2015", R.drawable.jabbe));
            items.add(new Item("Olliver","3 Jan, 2015", R.drawable.olliver));
            items.add(new Item("Huttu", "2014",R.drawable.huttu));
            items.add(new Item("Fring", "2014",R.drawable.gustavo_fring));
            items.add(new Item("Glader", "2014",R.drawable.happyface));
            items.add(new Item("Arger?", "2013",R.drawable.madface));
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;
            TextView name;
            TextView time;

            if(v == null)
            {
                v = inflater.inflate(R.layout.fragment_conversation_item, viewGroup, false);
                v.setTag(R.id.text2, v.findViewById(R.id.text2));
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.text);
            time = (TextView)v.getTag(R.id.text2);

            Item item = (Item)getItem(i);

            picture.setImageResource(item.drawableId);
            name.setText(item.name);
            time.setText(item.time);

            return v;
        }

        private class Item
        {
            final String time;
            final String name;
            final int drawableId;

            Item(String name, String time, int drawableId)
            {
                this.time = time;
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }

}
