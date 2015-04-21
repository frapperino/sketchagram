package sketchagram.chalmers.com.sketchagram;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ConversationViewFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        ImageView drawingView = (ImageView) view.findViewById(R.id.imageview);
        Drawing drawing = DrawingHolder.getInstance().getFirstDrawing();
        drawingView.setImageBitmap(drawing.getStaticDrawing());

        return view;
    }

}
