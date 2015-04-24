package sketchagram.chalmers.com.sketchagram;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Observable;
import java.util.Observer;


/**
 * Created by Bosch on 15/04/15.
 * A container for one message in a conversation.
 */

public class ConversationViewFragment extends Fragment implements View.OnClickListener, Observer{

    private int id;
    private ImageView mImageView;
    private DrawingView mDrawingView;
    private AMessage mMessage;

    public ConversationViewFragment() { id = 0; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        id = bundle.getInt("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        mImageView = (ImageView) view.findViewById(R.id.imageview);
        mImageView.setOnClickListener(this);
        mDrawingView = (DrawingView) view.findViewById(R.id.drawing);
        mDrawingView.addHelperObserver(this);
        mDrawingView.setTouchable(false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMessage = MessageHolder.getInstance().getMessage(id);
        mImageView.setImageBitmap(mMessage.getStaticDrawing());
    }

    @Override
    public void onClick(View v) {
        if(mImageView.getVisibility() == View.INVISIBLE) {
            mImageView.setVisibility(View.VISIBLE);
            mDrawingView.clearCanvas();
        } else if(mMessage instanceof Drawing){
            mImageView.setVisibility(View.INVISIBLE);
            mDrawingView.displayDrawing((Drawing) mMessage);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.e("FRAGMENT", "notified");
        mImageView.setVisibility(View.VISIBLE);
        mDrawingView.clearCanvas();
    }
}
