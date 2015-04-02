package sketchagram.chalmers.com.database;

import android.provider.BaseColumns;

/**
 * Created by Alex on 2015-03-06.
 */
public final class DataContract {

    private DataContract(){

    }

    public static abstract class ContactTable implements BaseColumns{
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_CONTACT_USERNAME = "contactusername";
        public static final String COLUMN_NAME_CONTACT_NAME = "contactname";
        public static final String COLUMN_NAME_CONTACT_EMAIL = "contactemail";
    }

    public static abstract class MessagesTable implements BaseColumns{
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_NAME_SENDER = "sender";
        public static final String COLUMN_NAME_RECEIVER = "receiver";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_TYPE = "type";
    }

}
