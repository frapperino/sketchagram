package sketchagram.chalmers.com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.database.DataContract.*;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.SystemUser;

/**
 * Created by Alex on 2015-03-06.
 */
public class SketchagramDb {

    private static final String COMMA = ", ";
    private static final String PRIMARY_KEY = "PRIMARY KEY ";
    private static final String TEXT = "TEXT ";
    private static final String INTEGER = "INTEGER ";
    private SQLiteDatabase db;
    private DBHelper dbh;

    public SketchagramDb (Context context) {
        dbh = new DBHelper(context);
        try {
            db = dbh.getWritableDatabase();
            dbh.onCreate(db);
        }catch (SQLiteException e){
            System.out.println(e.getMessage());

        }
    }

    public boolean insertContact  (String name, String email, String text)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactTable.COLUMN_NAME_CONTACT_ID, name);
        contentValues.put(ContactTable.COLUMN_NAME_CONTACT_NAME, email);
        contentValues.put(ContactTable.COLUMN_NAME_CONTACT_EMAIL, text);
        db.insert(ContactTable.TABLE_NAME, null, contentValues);
        return true;
    }

    public Integer deleteContact (String id)
    {
        return db.delete(ContactTable.TABLE_NAME,
                ContactTable.COLUMN_NAME_CONTACT_ID + " = " + id,
                new String[] { id });
    }

    public boolean updateContact (Integer id, String name, String email )
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put(ContactTable.COLUMN_NAME_CONTACT_ID, id);
        db.update(ContactTable.TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public ArrayList<Contact> getAllContacts()
    {
        ArrayList<Contact> array_list = new ArrayList();
        //hp = new HashMap();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String name = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUMN_NAME_CONTACT_NAME));
            String email = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUMN_NAME_CONTACT_EMAIL));
            String id = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUMN_NAME_CONTACT_ID));
            Profile profile = new Profile();
            profile.setNickName(email);
            profile.setFirstName(name);
            Contact c = new Contact(id, profile);
            array_list.add(c);
            res.moveToNext();
        }
        return array_list;
    }

}

