package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.SystemUser;


/**
 * A {@link Fragment} subclass. Used for allowing user to draw.
 * Activities that contain this fragment must implement the
 * {@link DrawingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DrawingFragment extends Fragment implements Observer {

    private OnFragmentInteractionListener mListener;

    private DrawingView drawView;

    private List<ADigitalPerson> receivers;
    private Drawing drawing = null;

    public DrawingFragment() {
        //android requires empty constructor
    }

    public static DrawingFragment newInstance(List<ADigitalPerson> receivers){
        DrawingFragment fragment = new DrawingFragment();
        fragment.receivers = receivers;
        return fragment;
    }

    public static DrawingFragment newInstance(Drawing drawing){
        DrawingFragment fragment = new DrawingFragment();
        fragment.drawing = drawing;
        return fragment;
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
        Drawing mDrawing = (Drawing)data;
        mDrawing.setStaticDrawing(drawView.getCanvasBitmapAsByte());
        ClientMessage<Drawing> message = new ClientMessage<>(System.currentTimeMillis(), SystemUser.getInstance().getUser(), receivers, mDrawing, MessageType.DRAWING);
        SystemUser.getInstance().getUser().sendMessage(message);
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
}
