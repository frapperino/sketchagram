

package sketchagram.chalmers.com.database;

/**
 * Created by alex on 2015-02-27.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import sketchagram.chalmers.com.database.DataContract.*;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;

public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;

    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String COMMA = ", ";
    private static final String PRIMARY_KEY = "PRIMARY KEY ";
    private static final String FOREIGN_KEY = "FOREIGN KEY ";
    private static final String REFERENCES = "REFERENCES ";
    private static final String TEXT = " TEXT ";
    private static final String INTEGER = " INTEGER ";
    private static final String BLOB = " BLOB ";
    private static final String AUTO_INCREMENT = "AUTO_INCREMENT ";
    private static final String DATABASE_NAME ="Sketchagram.db";

    private static final String  CONTACT_TABLE_CREATE = CREATE_TABLE + ContactTable.TABLE_NAME +" (" +
            ContactTable.COLUMN_NAME_CONTACT_USERNAME + TEXT + PRIMARY_KEY + COMMA +
            ContactTable.COLUMN_NAME_CONTACT_NAME + TEXT + COMMA +
            ContactTable.COLUMN_NAME_CONTACT_EMAIL + TEXT + ") ";

    private static final String MESSAGE_TABLE_CREATE = CREATE_TABLE + MessagesTable.TABLE_NAME + " (" +
            MessagesTable.COLUMN_NAME_CONTENT + BLOB + COMMA +
            MessagesTable.COLUMN_NAME_TIMESTAMP + INTEGER + COMMA +
            MessagesTable.COLUMN_NAME_TYPE + TEXT + COMMA +
            MessagesTable.COLUMN_NAME_SENDER + TEXT + COMMA +
            MessagesTable.COLUMN_NAME_RECEIVER + TEXT + COMMA +
            PRIMARY_KEY + "( " + MessagesTable.COLUMN_NAME_TIMESTAMP + COMMA + MessagesTable.COLUMN_NAME_SENDER + COMMA + MessagesTable.COLUMN_NAME_RECEIVER + " ) " + COMMA +
            FOREIGN_KEY + "( " + MessagesTable.COLUMN_NAME_SENDER + " ) " + REFERENCES + ContactTable.TABLE_NAME + "( " + ContactTable.COLUMN_NAME_CONTACT_USERNAME + ") " + COMMA +
            FOREIGN_KEY + "( " + MessagesTable.COLUMN_NAME_RECEIVER + " ) " + REFERENCES + ContactTable.TABLE_NAME + "( " + ContactTable.COLUMN_NAME_CONTACT_USERNAME + "))";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL( CONTACT_TABLE_CREATE  );
        db.execSQL( MESSAGE_TABLE_CREATE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db, MessagesTable.TABLE_NAME);
        dropTable(db, ContactTable.TABLE_NAME);
        onCreate(db);
    }
    public void onUpgrade(SQLiteDatabase db) {
        dropTable(db, MessagesTable.TABLE_NAME);
        dropTable(db, ContactTable.TABLE_NAME);
        onCreate(db);
    }

    public void dropTable (SQLiteDatabase db, String tbn){
        db.execSQL("DROP TABLE IF EXISTS " + tbn);
    }


    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ContactTable.TABLE_NAME);
        return numRows;
    }

}
