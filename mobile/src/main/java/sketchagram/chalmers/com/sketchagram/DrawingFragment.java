package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;


/**
 * A {@link Fragment} subclass. Used for allowing user to draw.
 * Activities that contain this fragment must implement the
 * {@link DrawingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DrawingFragment extends Fragment implements Observer {

    private OnFragmentInteractionListener mListener;

    private DrawingView drawView;

    private DrawingHelper helper;

    public DrawingFragment() {
        //android requires empty constructor
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
        helper = new DrawingHelper();
        drawView.setHelper(helper);
        helper.addObserver(this);
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
        drawView.startNew();
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
