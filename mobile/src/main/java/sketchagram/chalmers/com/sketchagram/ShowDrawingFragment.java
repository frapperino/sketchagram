package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.UserManager;


/**
 * A {@link Fragment} subclass. Used for allowing user to draw.
 * Activities that contain this fragment must implement the
 * {@link DrawingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShowDrawingFragment extends Fragment implements Observer {

    private OnFragmentInteractionListener mListener;

    private DrawingView drawView;

    private ClientMessage msg;
    private List<Contact> receivers;
    private Drawing drawing = null;

    public ShowDrawingFragment() {
        //android requires empty constructor
    }

    public static ShowDrawingFragment newInstance(List<Contact> receivers){
        ShowDrawingFragment showFragment = new ShowDrawingFragment();
        showFragment.receivers = receivers;
        return showFragment;
    }

    public static ShowDrawingFragment newInstance(Drawing drawing){
        ShowDrawingFragment showFragment = new ShowDrawingFragment();
        showFragment.drawing = drawing;
        return showFragment;
    }

    public static ShowDrawingFragment newInstance(Drawing drawing, ClientMessage msg){
        ShowDrawingFragment showFragment = new ShowDrawingFragment();
        showFragment.drawing = drawing;
        showFragment.msg = msg;
        return showFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);
        //Get view that is displayed in the Activity on which we can call
        //the methods in the DrawingView class.
        drawView = (DrawingView) view.findViewById(R.id.drawing);
        drawView.addHelperObserver(this);
        if(drawing != null){
            drawView.displayDrawing(drawing);
            drawView.setTouchable(false);
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
    public void update(Observable observable, Object data) {
        Drawing drawing = (Drawing)data;
        UserManager.getInstance().sendMessage(receivers, drawing);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame, new ConversationFragment())
                .addToBackStack(null).commit();
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
        public void onFragmentInteraction(Uri uri);
    }


    private void showGlobalContextActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(false);
        final ImageButton actionBarIcon1 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon1);
        actionBarIcon1.setImageResource(R.drawable.ic_action_back);
        TextView actionBarTitle = (TextView) getActivity().findViewById(R.id.action_bar_title);
        actionBarTitle.setText(msg.getSender().getUsername().toString() +"'s drawing");
        actionBarTitle.setPadding(25, 0, 0, 0);
        ImageButton actionBarIcon2 = (ImageButton) getActivity().findViewById(R.id.action_bar_icon2);
        actionBarIcon2.setImageResource(R.drawable.ic_action_view_as_grid);

        actionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                fm.popBackStack();
            }
        });

        actionBarIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                fm.popBackStack();
            }
        });

        actionBarIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_frame, new ConversationFragment())
                        .addToBackStack(null).commit();
            }
        });
    }
    private android.support.v7.app.ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}