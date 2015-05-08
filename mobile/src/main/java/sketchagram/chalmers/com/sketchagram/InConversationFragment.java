package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import sketchagram.chalmers.com.model.ClientMessage;
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
public class InConversationFragment extends Fragment implements AbsListView.OnItemClickListener {
    private OnFragmentInteractionListener mListener;

    private static final String PARAM1 = "conversation ID";

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private int conversationId;
    private Conversation conversation;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     * Use newInstance instead.
     */
    public InConversationFragment() {
    }

    public static InConversationFragment newInstance(int param1) {
        InConversationFragment fragment = new InConversationFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) {
            conversationId = bundle.getInt(PARAM1);
            conversation = UserManager.getInstance().getConversation(conversationId);
            mAdapter = new InConversationListAdapter(getActivity(), conversation.getHistory());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inconversation, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        final android.support.v7.app.ActionBar actionBar = getActionBar();
        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_actionbar);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        final ImageButton reply = (ImageButton) view.findViewById(R.id.reply_button);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement actual reply to self
                if(conversation.getOtherParticipants().size() > 0){
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_fragment_frame, DrawingFragment.newInstance(conversation.getOtherParticipants()))
                            .addToBackStack(null).commit();
                }
                else{
                    Toast.makeText(MyApplication.getContext(), "Can not reply to self.", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            ClientMessage message = conversation.getHistory().get(position);
            //TODO show emoticons when messagetype == Emoticons
            if(message.getType() == MessageType.DRAWING){
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment_frame, ShowDrawingFragment.newInstance((Drawing)message.getContent(), message))
                        .addToBackStack(null).commit();
            }
        }
    }

    /**
     * Update list graphically when model has changed.
     */
    public void updateList() {
        if(mAdapter != null) {
            ((BaseAdapter)mAdapter).notifyDataSetChanged();
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
    private void showGlobalContextActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(false);
        ImageButton actionBarIcon1 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon1);
        actionBarIcon1.setImageResource(R.drawable.ic_action_back);
        TextView actionBarTitle = (TextView) getActivity().findViewById(R.id.action_bar_title);
        if(conversation.getOtherParticipants().size() == 0){
            actionBarTitle.setText(UserManager.getInstance().getUsername().toString());
        } else{
            actionBarTitle.setText(conversation.getOtherParticipants().get(0).getUsername().toString());
        }
        //actionBarTitle.setPadding(25,0,0,0);
        ImageButton actionBarIcon2 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon2);
        actionBarIcon2.setImageResource(0);

        actionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment_frame, new ConversationFragment())
                        .addToBackStack(null).commit();
            }
        });

        actionBarIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fragment_frame, new ConversationFragment())
                        .addToBackStack(null).commit();
            }
        });

    }
    private android.support.v7.app.ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }


    //---------------------------ADAPTER---------------------------------

    public class InConversationListAdapter extends ArrayAdapter<ClientMessage> {
        private Context context;
        private List<ClientMessage> messages;
        private final int IMAGE_SIZE = 400;

        public InConversationListAdapter(Context context, List<ClientMessage> items) {
            super(context, android.R.layout.simple_list_item_1, items);
            this.messages = items;
            this.context = context;
        }

        /**
         * Holder for the list items.
         */
        private class ViewHolder{
            TextView titleText;
            TextView dateText;
            ImageView drawing;
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
            ClientMessage item = (ClientMessage) getItem(position);
            View viewToUse;

            // This block exists to inflate the settings list item conditionally based on whether
            // we want to support a grid or list view.
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                viewToUse = mInflater.inflate(R.layout.inconversation_list_item, null);
                holder = new ViewHolder();
                holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
                holder.dateText = (TextView)viewToUse.findViewById(R.id.dateTextView);
                holder.drawing = (ImageView) viewToUse.findViewById(R.id.drawingToShow);
                viewToUse.setTag(holder);
            } else {
                viewToUse = convertView;
                holder = (ViewHolder) viewToUse.getTag();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultDate = new Date(item.getTimestamp());

            holder.dateText.setText(sdf.format(resultDate));
            holder.titleText.setText(item.getSender().getUsername().toString());
            if(item.getContent() instanceof Emoticon) {
                holder.drawing.setImageResource(((Emoticon)item.getContent()).getEmoticonType().getDrawable());
            } else if(item.getContent() instanceof Drawing) {
                holder.drawing.setImageBitmap(((Drawing) item.getContent()).getStaticDrawing(IMAGE_SIZE, IMAGE_SIZE));
            } else {
                throw new IllegalStateException("inConversationFragment attempt to display unsupported content type");
            }
            //TODO: check if drawing or smiley
            if (item.getSender().getUsername().toLowerCase().equals(UserManager.getInstance().getUsername().toLowerCase())) {
                holder.titleText.setText(UserManager.getInstance().getUsername().toString());
            }
            item.setRead(true); //Mark message as read.
            return viewToUse;
        }
    }



}