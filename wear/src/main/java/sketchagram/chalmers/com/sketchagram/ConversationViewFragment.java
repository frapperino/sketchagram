package sketchagram.chalmers.com.sketchagram;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ConversationViewFragment extends Fragment {

    private int id;
    private ImageView mImageView;

    public ConversationViewFragment() { id = 0; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        id = bundle.getInt("id");
        Log.e("FRAGMENTID", "" + id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        mImageView = (ImageView) view.findViewById(R.id.imageview);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Drawing drawing = DrawingHolder.getInstance().getDrawing(id);
        mImageView.setImageBitmap(drawing.getStaticDrawing());
    }

}
