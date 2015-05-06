package sketchagram.chalmers.com.sketchagram;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.UserManager;

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
        conversationList = UserManager.getInstance().getAllConversations();
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

        //initiate the custom toolbar, used here since this is the first fragment with the actionbar
        android.support.v7.app.ActionBar actionBar = getActionBar();
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_actionbar);
        showGlobalContextActionBar();
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
        if(adapter != null) {
            adapter.notifyDataSetChanged();
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
                    boolean isRead = c.hasUnreadMessages();
                    if(lastMessage.getType() == MessageType.DRAWING) {
                        items.add(new Item(c.toString(),
                               lastMessage.dateToShow(),
                                ((Drawing)lastMessage.getContent()).getStaticDrawing(IMAGE_SIZE, IMAGE_SIZE), isRead));
                    } else {
                        items.add(new Item(c.toString(),
                                history.get(history.size()-1).dateToShow(), null, isRead));
                    }
                    if(c.hasUnreadMessages()){
                        //TODO 
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
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.senderDate, v.findViewById(R.id.senderDate));
                v.setTag(R.id.senderText, v.findViewById(R.id.senderText));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.senderText);
            time = (TextView)v.getTag(R.id.senderDate);

            Item item = (Item)getItem(i);

            if(item.hasUnreadMessages()) {  //Highlight unread conversations.
                time.setTypeface(null, Typeface.BOLD);
                name.setTypeface(null, Typeface.BOLD);
            } else {
                time.setTypeface(null, Typeface.NORMAL);
                name.setTypeface(null, Typeface.NORMAL);
            }

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
            final boolean isRead;

            Item(String name, String time, Bitmap drawing, boolean isRead) {
                this.time = time;
                this.name = name;
                this.drawing = drawing;
                this.isRead = isRead;
            }
            public boolean hasUnreadMessages(){
                return isRead;
            }

        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showGlobalContextActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(false);
        ImageButton actionBarIcon1 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon1);
        actionBarIcon1.setImageResource(R.drawable.ic_action_cancel); //use our logo here with the right sizes
        TextView actionBarTitle = (TextView) getActivity().findViewById(R.id.action_bar_title);
        actionBarTitle.setText("Conversations");
        //actionBarTitle.setPadding(25,0,0,0);
        ImageButton actionBarIcon2 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon2);
        actionBarIcon2.setImageResource(R.drawable.ic_action_cc_bcc);

        actionBarIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_frame, new ContactManagementFragment())
                        .addToBackStack(null).commit();
            }
        });
    }
    private android.support.v7.app.ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}
