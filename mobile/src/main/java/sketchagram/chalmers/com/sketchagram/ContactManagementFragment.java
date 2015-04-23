package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import sketchagram.chalmers.com.model.Contact;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ContactManagementFragment extends Fragment implements AbsListView.OnItemClickListener, Observer {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private List<Contact> contactList;

    // TODO: Rename and change types of parameters
    public static ContactManagementFragment newInstance(String param1, String param2) {
        ContactManagementFragment fragment = new ContactManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactManagementFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        contactList = MyApplication.getInstance().getUser().getContactList();
        // Sets the adapter to customized one which enables our layout of items.
        mAdapter = new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_list_item_1, contactList);
        MyApplication.getInstance().getUser().addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Collections.sort(MyApplication.getInstance().getUser().getContactList(),new Comparator<Contact>() {
            //Sort alphabetically

            @Override
            public int compare(Contact lhs, Contact rhs) {
                int result = String.CASE_INSENSITIVE_ORDER.compare(lhs.getUsername(), rhs.getUsername());
                return (result != 0) ? result : lhs.getUsername().compareTo(rhs.toString());
            }
        });
        View view = inflater.inflate(R.layout.fragment_contact_management_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.contact_management_list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        registerForContextMenu(mListView);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.contact_management_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(contactList.get(info.position).getUsername());
            String[] menuItems = getResources().getStringArray(R.array.contact_menu_items);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Contact removedContact = contactList.get(info.position);
        String contactName = removedContact.getUsername();
        boolean success = MyApplication.getInstance().getUser().removeContact(removedContact);
        if(success) {
            Toast.makeText(MyApplication.getContext(), contactName + " was removed from contacts.", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MyApplication.getContext(), contactName + " couldn't be removed.", Toast.LENGTH_SHORT).show();
        }
        BaseAdapter adapter = (BaseAdapter)mAdapter;
        adapter.notifyDataSetChanged();
        return success;
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
            mListener.onFragmentInteraction(MyApplication.getInstance().getUser().getContactList().get(position).toString());
        }
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

    @Override
    public void update(Observable observable, Object data) {
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
        public void onFragmentInteraction(String id);
    }

    public void updateList(){
        if(mAdapter != null) {
            Collections.sort(MyApplication.getInstance().getUser().getContactList(),new Comparator<Contact>() {
                //Sort alphabetically

                @Override
                public int compare(Contact lhs, Contact rhs) {
                    int result = String.CASE_INSENSITIVE_ORDER.compare(lhs.getUsername(), rhs.getUsername());
                    return (result != 0) ? result : lhs.getUsername().compareTo(rhs.toString());
                }
            });
            BaseAdapter adapter = (BaseAdapter) mAdapter;
            adapter.notifyDataSetChanged();
        }
    }
}
