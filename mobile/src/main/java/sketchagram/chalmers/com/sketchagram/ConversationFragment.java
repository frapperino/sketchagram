package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;
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

import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.MessageType;

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
    private GridView gridView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private List<Conversation> conversationList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConversationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversationList = MyApplication.getInstance().getUser().getConversationList();
        mAdapter = new MyAdapter(getActivity(), conversationList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        gridView = (GridView) view.findViewById(R.id.conversation_list);
        gridView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        gridView.setOnItemClickListener(this);

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
            mListener.onFragmentInteraction(conversationList.get(position).getConversationId());
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
        public void onFragmentInteraction(int conversationId);
    }

    private class MyAdapter extends BaseAdapter {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        private final int IMAGE_SIZE = 400;

        public MyAdapter(Context context, List<Conversation> conversations) {
            inflater = LayoutInflater.from(context);
            if(conversations != null){
                for (Conversation c: conversations){
                    List<ClientMessage> history = c.getHistory();
                    ClientMessage lastMessage = c.getHistory().get(history.size()-1);
                    if(lastMessage.getType() == MessageType.DRAWING) {
                        items.add(new Item(c.getParticipants().get(0).getUsername().toString(),
                               lastMessage.dateToShow(),
                                ((Drawing)lastMessage.getContent()).getStaticDrawing(IMAGE_SIZE, IMAGE_SIZE)));
                    } else {
                        items.add(new Item(c.getParticipants().get(0).getUsername().toString(),
                                history.get(history.size()-1).dateToShow(), null));
                    }
                }
            }
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
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;
            TextView time;

            if(v == null) {
                v = inflater.inflate(R.layout.fragment_conversation_item, viewGroup, false);
                v.setTag(R.id.text2, v.findViewById(R.id.text2));
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.text);
            time = (TextView)v.getTag(R.id.text2);

            Item item = (Item)getItem(i);

            if(item.drawing != null) {
                picture.setImageBitmap(item.drawing);
            }
            name.setText(item.name);
            time.setText(item.time);

            return v;
        }

        private class Item {
            final String time;
            final String name;
            final Bitmap drawing;

            Item(String name, String time, Bitmap drawing) {
                this.time = time;
                this.name = name;
                this.drawing = drawing;
            }
        }
    }
}
