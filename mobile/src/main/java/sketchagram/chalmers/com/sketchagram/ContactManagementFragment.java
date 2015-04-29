package sketchagram.chalmers.com.sketchagram;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;

import quickscroll.QuickScroll;
import quickscroll.Scrollable;
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

    private List<Contact> contactList;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ExpandableListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ExpandableListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactManagementFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Replace with UserManager after merge.
        contactList = MyApplication.getInstance().getUser().getContactList();

        // Sets the adapter to customized one which enables our layout of items.
        mAdapter = new ExpandableAlphabeticalAdapter(getActivity(), contactList);
        //new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_list_item_1, contactList);
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
        View view = inflater.inflate(R.layout.fragment_contact_management, container, false);

        // Set the adapter
        mListView = (ExpandableListView) view.findViewById(R.id.contact_management_list_view);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        registerForContextMenu(mListView);

        setupExpandableListView();
        setupQuickScroll(view);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    private void setupQuickScroll(View view) {
        //TODO: Setup proper colors to match the rest of application.
        final int PURPLE = Color.parseColor("#9C27B0");
        final int PURPLE_DARK = Color.parseColor("#673AB7");
        final int PURPLE_HANDLE = Color.parseColor("#8E24AA");

        //Initialize quickscroll
        final QuickScroll quickscroll = (QuickScroll) view.findViewById(R.id.quickscroll);
        quickscroll.init(QuickScroll.TYPE_INDICATOR_WITH_HANDLE, mListView, (ExpandableAlphabeticalAdapter)mAdapter, QuickScroll.STYLE_HOLO);
        quickscroll.setIndicatorColor(PURPLE, PURPLE_DARK, Color.WHITE);
        quickscroll.setHandlebarColor(PURPLE, PURPLE, PURPLE_HANDLE);
        quickscroll.setFixedSize(2);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //TODO check if group or child. Group should not be clickable.
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.contact_management_list_view && ((ExpandableListView)v).getPackedPositionType(((ExpandableListView.ExpandableListContextMenuInfo)menuInfo).packedPosition) != ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(contactList.get(info.position).getUsername());
            String[] menuItems = getResources().getStringArray(R.array.contact_menu_items);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    private void setupExpandableListView() {
        //Set all groups expanded
        int count = mAdapter.getGroupCount();
        for(int i=0; i < count; i++)
            mListView.expandGroup(i);

        //Set an onClickListener to avoid collapsing.
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });
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

    /**
     * Adapter used for sorting in alphabetical order.
     */
    private class ExpandableAlphabeticalAdapter extends BaseExpandableListAdapter implements Scrollable {
        //TODO: Refactor logic to use map only and remove sections variable.
        private HashMap<String, List<Contact>> alphaIndexer;
        private List<String> sections;
        private LayoutInflater inflater;

        public ExpandableAlphabeticalAdapter(Context context, List<Contact> contactList) {
            inflater = LayoutInflater.from(context);
            alphaIndexer = new HashMap<>();
            sections = new ArrayList();
            for (Contact c: contactList) {
                String s = c.getUsername().substring(0, 1).toUpperCase();
                if(!alphaIndexer.containsKey(s)) {
                    sections.add(s);
                    List<Contact> contacts = new ArrayList<>();
                    contacts.add(c);
                    alphaIndexer.put(s, contacts);
                } else {
                    List<Contact> entries = alphaIndexer.get(s);
                    entries.add(c);
                    Collections.sort(entries);
                    alphaIndexer.put(s, entries);
                }
                Collections.sort(sections);
            }
            Log.d("TEST", "TEST");
        }

        private Bitmap getCircleBitmap(Bitmap bitmap) {
            final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);

            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            bitmap.recycle();

            return output;
        }

        @Override
        public String getIndicatorForPosition(int childposition, int groupposition) {
            return sections.get(groupposition);
        }

        @Override
        public int getScrollPosition(int childposition, int groupposition) {
            return childposition;
        }

        @Override
        public int getGroupCount() {
            return alphaIndexer.keySet().size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return alphaIndexer.get(sections.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return sections.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            List<Contact> entries = alphaIndexer.get(sections.get(groupPosition));
            return entries.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_contact_management_group_item, parent, false);
            }
            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.list_group);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(sections.get(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Contact contact = alphaIndexer.get(sections.get(groupPosition)).get(childPosition);
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_contact_management_list_item, parent, false);
                convertView.setTag(R.id.rounded_contact_image, convertView.findViewById(R.id.rounded_contact_image));
                convertView.setTag(R.id.contact_name, convertView.findViewById(R.id.contact_name));
                convertView.setTag(R.id.status_image, convertView.findViewById(R.id.status_image));
            }

            ImageView roundedImage = (ImageView)convertView.getTag(R.id.rounded_contact_image);
            TextView contactName = (TextView)convertView.getTag(R.id.contact_name);
            ImageView statusImage = (ImageView)convertView.getTag(R.id.status_image);

            contactName.setText(contact.getUsername());

            Bitmap bitmap = contact.getProfile().getImage();
            if(bitmap == null) {    // Use default image from resources
                bitmap = getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.profile));
            }
            roundedImage.setImageBitmap(bitmap);

            switch(contact.getStatus()) {
                case ONLINE:
                    statusImage.setBackgroundColor(Color.GREEN);
                case OFFLINE:
                    statusImage.setBackgroundColor(Color.RED);
                case AWAY:
                    statusImage.setBackgroundColor(Color.YELLOW);
                default:
                    statusImage.setBackgroundColor(Color.WHITE);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
