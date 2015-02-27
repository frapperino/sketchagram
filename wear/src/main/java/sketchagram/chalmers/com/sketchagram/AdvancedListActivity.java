package sketchagram.chalmers.com.sketchagram;

/**
 * Created by Bosch on 27/02/15.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdvancedListActivity extends Activity implements WearableListView.ClickListener  {

    private WearableListView mListView;
    private MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_stub);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.listView1);
                loadAdapter();
            }
        });
    }

    private void loadAdapter(){
        mAdapter = new MyListAdapter(this, listItems);
        LinearLayoutManager mManager = new LinearLayoutManager(getApplicationContext());
        mListView.setLayoutManager(mManager);
        mListView.setAdapter(mAdapter);
        mListView.setClickListener(AdvancedListActivity.this);
    }

    private static ArrayList<Integer> listItems;
    static {
        listItems = new ArrayList<Integer>();
        listItems.add(R.drawable.content_icon_small);
        listItems.add(R.drawable.content_icon_small);
        listItems.add(R.drawable.content_icon_small);
        listItems.add(R.drawable.content_icon_small);
        listItems.add(R.drawable.content_icon_small);
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Toast.makeText(this, String.format("You selected item #%s", viewHolder.getPosition()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTopEmptyRegionClick() {
        Toast.makeText(this, "You tapped Top empty area", Toast.LENGTH_SHORT).show();
    }

    public class MyListAdapter extends WearableListView.Adapter {

        private final Context context;
        private final List<Integer> items;

        public MyListAdapter(Context context, List<Integer> items) {
            this.context = context;
            this.items = items;
        }
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new MyItemView(AdvancedListActivity.this));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            //TODO: Add content without getting nullpointers
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private final class MyItemView extends FrameLayout implements WearableListView.OnCenterProximityListener{

        final ImageView image;
        final TextView txtView;

        public MyItemView(Context context) {
            super(context);
            View.inflate(context, R.layout.fragment_message, this);
            image = (ImageView) findViewById(R.id.image);
            txtView = (TextView) findViewById(R.id.text);
        }

        @Override
        public void onCenterPosition(boolean b) {
            image.animate().scaleX(1f).scaleY(1f).alpha(1);
            txtView.animate().scaleX(1f).scaleY(1f).alpha(1);

        }

        @Override
        public void onNonCenterPosition(boolean b) {

            image.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
            txtView.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        }
    }
}