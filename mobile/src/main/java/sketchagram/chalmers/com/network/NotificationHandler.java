package sketchagram.chalmers.com.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.sketchagram.MainActivity;
import sketchagram.chalmers.com.sketchagram.R;

/**
 * Used for creating notifications in the app.
 * Created by Alexander on 2015-04-02.
 */
public class NotificationHandler {
    private Context context;
    private NotificationManager notificationManager;
    int messageId = 0;        //allows you to update the notification later on.

    public NotificationHandler(Context context) {
        this.context = context;
        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Creates a notification displaying that a new message has been received.
     */
    public void pushNewMessageNotification(Conversation conversation, ClientMessage message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setSmallIcon(R.drawable.sketchagram_logo);  //TODO: Provide photo of sender.
        mBuilder.setContentTitle(message.getSender().getUsername());
        mBuilder.setContentText(message.getContent().toString());  //TODO: Display message content.
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
        mBuilder.setLights(Color.MAGENTA, 500, 500);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("ConversationId", conversation.getConversationId());

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(messageId, mBuilder.build());
    }
}
