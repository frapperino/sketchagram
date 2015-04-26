package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

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
            conversation = MyApplication.getInstance().getUser().getConversation(conversationId);
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

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
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
            if(message.getType() == MessageType.DRAWING){
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_frame, DrawingFragment.newInstance((Drawing)message.getContent()))
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
        final ImageButton actionBarIcon1 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon1);
        actionBarIcon1.setBackgroundResource(R.drawable.ic_action_previous_item);
        TextView actionBarTitle = (TextView) getActivity().findViewById(R.id.action_bar_title);
        actionBarTitle.setText(conversation.getParticipants().get(0).getUsername().toString());
        actionBarTitle.setPadding(25,0,0,0);
        ImageButton actionBarIcon2 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon2);
        actionBarIcon2.setBackgroundResource(0);
    }
    private android.support.v7.app.ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}
