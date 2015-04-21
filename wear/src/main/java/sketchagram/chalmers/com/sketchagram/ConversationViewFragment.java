package sketchagram.chalmers.com.sketchagram;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;

public class ConversationViewFragment extends Fragment implements Observer {

    private DrawingHelper helper;
    private DrawingView drawingView;
    private Drawing drawing;
    private boolean displayed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        drawingView = (DrawingView) view.findViewById(R.id.drawing);
        helper = new DrawingHelper();
        helper.addObserver(this);
        drawingView.setHelper(helper);
        displayed = false;
        return view;
    }

    private void displayDrawing() {
        if(!displayed)
            drawingView.displayDrawing(drawing);
        displayed = true;
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.e("Drawing", "update");
        if(drawing == null) {
            Log.e("Drawing", "null");
            if(DrawingHolder.getInstance().getDrawings() != null) {
                drawing = DrawingHolder.getInstance().getFirstDrawing();
                drawingView.displayDrawing(drawing);
            }
        }
    }
}
