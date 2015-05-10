package sketchagram.chalmers.com.sketchagram;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.Emoticon;
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
        mAdapter = new MyAdapter(getActivity());
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

        TextView noMessages;
        noMessages = (TextView) view.findViewById(R.id.noMessages);

        if(UserManager.getInstance().getAllConversations().size() == 0){
            noMessages.setVisibility(View.VISIBLE);
            noMessages.setText("You have no conversations, start a new by clicking the blue botton below.");
        } else {
            noMessages.setVisibility(View.INVISIBLE);
        }

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
        private LayoutInflater inflater;

        private final int IMAGE_SIZE = 300;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            /*if(conversations != null){
                for (Conversation c: conversations){
                    List<ClientMessage> history = c.getHistory();
                    ClientMessage lastMessage = c.getHistory().get(history.size()-1);
                    boolean isRead = c.hasUnreadMessages();
                    if(lastMessage.getType() == MessageType.DRAWING) {
                        items.add(new Item(c.toString(),
                               lastMessage.dateToShow(),
                                ((Drawing)lastMessage.getContent()).getStaticDrawing(IMAGE_SIZE, IMAGE_SIZE), isRead));
                    }
                    else if(lastMessage.getType() == MessageType.EMOTICON){
                        int emote = ((Emoticon) lastMessage.getContent()).getEmoticonType().getDrawable();
                        items.add(new Item(c.toString(),
                                lastMessage.dateToShow(),
                                (BitmapFactory.decodeResource(getActivity().getResources(), emote)), isRead));
                    } else{
                        items.add(new Item(c.toString(),
                                history.get(history.size()-1).dateToShow(), null, isRead));
                    }
                    if(c.hasUnreadMessages()){
                        //TODO 
                    }*/
        }

        @Override
        public int getCount() {
            return conversationList.size();
        }

        @Override
        public Object getItem(int position) {
            return conversationList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;
            TextView time;
            ImageView unreadMessage;
            ImageView fast_reply;
            ImageView status_image_grid;

            if(v == null) {
                v = inflater.inflate(R.layout.fragment_conversation_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.senderDate, v.findViewById(R.id.senderDate));
                v.setTag(R.id.senderText, v.findViewById(R.id.senderText));
                v.setTag(R.id.unreadMessage, v.findViewById(R.id.unreadMessage));
                v.setTag(R.id.fast_reply, v.findViewById(R.id.fast_reply));
                v.setTag(R.id.status_image_grid, v.findViewById(R.id.status_image_grid));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.senderText);
            time = (TextView)v.getTag(R.id.senderDate);
            unreadMessage = (ImageView) v.getTag(R.id.unreadMessage);
            fast_reply = (ImageView) v.getTag(R.id.fast_reply);
            status_image_grid = (ImageView) v.getTag(R.id.status_image_grid);

            final Contact contact = UserManager.getInstance().getAllContacts().get(i);
            final Conversation conversation = (Conversation) getItem(i);
            List<ClientMessage> history = conversation.getHistory();
            ClientMessage lastMessage = conversation.getHistory().get(history.size()-1);

            if(conversation.hasUnreadMessages()) {  //Highlight unread conversations.
                unreadMessage.setVisibility(View.VISIBLE);
            } else {
                unreadMessage.setVisibility(View.INVISIBLE);
            }

            if(lastMessage.getContent() instanceof Drawing) {
                picture.setImageBitmap(((Drawing)lastMessage.getContent()).getStaticDrawing(IMAGE_SIZE, IMAGE_SIZE));
            } else if(lastMessage.getContent() instanceof Emoticon) {
                picture.setImageResource((((Emoticon) lastMessage.getContent()).getEmoticonType().getDrawable()));
            }
            name.setText(conversation.toString());
            time.setText(lastMessage.dateToShow());

            fast_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: does this always work?
                    if(!(contact.getUsername().toLowerCase().equals(UserManager.getInstance().getUsername().toLowerCase()))){
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_fragment_frame, DrawingFragment.newInstance(conversation.getOtherParticipants()))
                                .addToBackStack(null).commit();
                    }else{
                        Toast.makeText(MyApplication.getContext(), "Can not reply to self.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if(contact.getStatus() != null) {
                switch(contact.getStatus()) {
                    case ONLINE:
                        status_image_grid.setBackgroundResource(R.drawable.status_online);
                        break;
                    case OFFLINE:
                        status_image_grid.setBackgroundResource(R.drawable.status_offline);
                        break;
                    case AWAY:
                        status_image_grid.setBackgroundResource(R.drawable.status_away);
                        break;
                    default:
                        status_image_grid.setBackgroundColor(Color.WHITE);
                        break;
                }
            }


            return v;
        }



    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showGlobalContextActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(false);
        ImageButton actionBarIcon1 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon1);
        actionBarIcon1.setImageResource(0); //use our logo here with the right sizes
        TextView actionBarTitle = (TextView) getActivity().findViewById(R.id.action_bar_title);
        actionBarTitle.setText("Conversations");
        //actionBarTitle.setPadding(25,0,0,0);
        ImageButton actionBarIcon2 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon2);
        actionBarIcon2.setImageResource(R.drawable.ic_action_cc_bcc);

        actionBarIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment_frame, new ContactManagementFragment())
                        .addToBackStack(null).commit();
            }
        });
    }
    private android.support.v7.app.ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}
