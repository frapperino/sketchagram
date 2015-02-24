package sketchagram.chalmers.com.database;
import android.provider.BaseColumns;


/**
 * Created by alex on 2015-02-24.
 */
public class TableData {

    public TableData()
    {

    }

    public static abstract class TableInfo implements BaseColumns
    {

        public static final String User_Name = "user_name" ;
        public static final String User_pass = "user_pass" ;
        public static final String Email = "email" ;
        public static final String Database_Name = "Conversation_info" ;
        public static final String Table_Name= "User_info" ;


    }
}



