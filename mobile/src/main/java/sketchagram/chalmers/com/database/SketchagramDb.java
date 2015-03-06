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
        }catch (SQLiteException e){

        }
    }

    public boolean insertContact  (String name, String email, String text)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("text", text);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Integer deleteContact (String id)
    {
        return db.delete(ContactTable.TABLE_NAME,
                ContactTable.COLUM_NAME_CONTACT_ID + " = " + id,
                new String[] { id });
    }

    public boolean updateContact (Integer id, String name, String email )
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("id", id);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public ArrayList<Contact> getAllContacts()
    {
        ArrayList<Contact> array_list = new ArrayList();
        //hp = new HashMap();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String name = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUM_NAME_CONTACT_NAME));
            String email = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUM_NAME_CONTACT_EMAIL));
            String id = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUM_NAME_CONTACT_ID));
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

