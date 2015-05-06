package sketchagram.chalmers.com.sketchagram;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * Created by Bosch on 15/04/15.
 * A container for one message in a conversation.
 */

public class ConversationViewFragment extends Fragment implements View.OnClickListener{

    private int id;
    private String contact;
    private ImageView mMessageView;
    private ImageView mReplyView;
    private AMessage mMessage;

    public ConversationViewFragment() { id = 0; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        id = bundle.getInt("id");
        contact = ((ConversationViewActivity) getActivity()).getContact();
        Log.d("Contact in fragment", contact);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        mMessageView = (ImageView) view.findViewById(R.id.imageView);
        mMessageView.setOnClickListener(this);
        mReplyView = (ImageView) view.findViewById(R.id.replyView);
        mReplyView.setOnClickListener(this);
        mReplyView.setVisibility(View.INVISIBLE);
        mReplyView.setBackgroundResource(R.drawable.ic_return);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMessage = MessageHolder.getInstance().getMessage(id);
        mMessageView.setImageBitmap(mMessage.getStaticDrawing());
    }

    @Override
    public void onClick(View v) {
        if(mReplyView.getVisibility() == View.VISIBLE){
            mReplyView.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(getActivity(), DrawingActivity.class);
            intent.putExtra(BTCommType.SEND_TO_CONTACT.toString(), contact);
            startActivity(intent);
            getActivity().finish();
        } else
            mReplyView.setVisibility(View.VISIBLE);
    }
}
