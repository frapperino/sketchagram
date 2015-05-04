package sketchagram.chalmers.com.database;

import android.provider.BaseColumns;

/**
 * Created by Alex and Olliver on 2015-03-06.
 */
public final class DataContract {

    private DataContract(){

    }

    public static abstract class ContactTable implements BaseColumns{
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_CONTACT_USERNAME = "contactusername";
        public static final String COLUMN_NAME_CONTACT_NAME = "contactname";
        public static final String COLUMN_NAME_CONTACT_EMAIL = "contactemail";
        public static final String COLUMN_NAME_LAST_ACCESSED = "lastaccessed";
    }

    public static abstract class MessagesTable implements BaseColumns{
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_NAME_SENDER = "sender";
        public static final String COLUMN_NAME_CONVERSATION_ID = "conversationid";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_READ = "read";
    }

    public static abstract class ConversationTable implements BaseColumns{
        public static final String TABLE_NAME = "conversations";
        public static final String COLUMN_NAME_CONVERSATION_ID = "conversationid";
        public static final String COLUMN_NAME_PARTICIPANT = "participant";
    }

}
