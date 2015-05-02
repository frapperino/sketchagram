package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.plus.PlusOneButton;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.Emoticon;
import sketchagram.chalmers.com.model.EmoticonType;
import sketchagram.chalmers.com.model.MessageType;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link EmoticonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmoticonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmoticonFragment extends Fragment implements OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListAdapter mAdapter;

    private ListView listView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment EmoticonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmoticonFragment newInstance() {
        EmoticonFragment fragment = new EmoticonFragment();
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
        Toast.makeText(getActivity(), "Emoticon pressed.", Toast.LENGTH_LONG).show();
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

    private class MyAdapter extends BaseAdapter {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        private final int IMAGE_SIZE = 400;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            EmoticonType[] emoticonTypes = EmoticonType.values();
            for(int i=0; i<emoticonTypes.length; i++) {
                items.add(new Item(new Emoticon(emoticonTypes[i])));
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
            ImageButton emoticonButton = (ImageButton)view.findViewById(R.id.emoticonButton);
            emoticonButton.setImageResource(((Item)getItem(i)).emoticon.getEmoticonType().getDrawable());
            return view;
        }

        private class Item {
            final Emoticon emoticon;

            Item(Emoticon emoticon) {
                this.emoticon = emoticon;
            }
        }
    }
}
