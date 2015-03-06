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
        public static final String COLUM_NAME_CONTACT_ID = "contactid";
        public static final String COLUM_NAME_CONTACT_NAME = "contactname";
        public static final String COLUM_NAME_CONTACT_EMAIL = "contactemail";
    }
}
