package sketchagram.chalmers.com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.MotionEvent;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.database.DataContract.*;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.DrawingEvent;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.model.User;

/**
 * Created by Alex on 2015-03-06.
 */
public class SketchagramDb {

    private static final String WHERE = " WHERE ";
    private static final String FROM = " FROM ";
    private static final String SELECT_ALL = "SELECT * ";
    private static final String EQUALS = " = ";
    private static final String AND = " AND ";
    private static final String QUESTION_MARK = " ? ";
    private static final String OR = " OR ";
    private SQLiteDatabase db;
    private DBHelper dbh;

    public SketchagramDb (Context context) {
        dbh = new DBHelper(context);

        try {
            db = dbh.getWritableDatabase();
        }catch (SQLiteException e){
            System.out.println(e.getMessage());

        }
    }

    public void update() {
        dbh.onUpgrade(db);
    }
    public boolean insertContact  (Contact contact)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactTable.COLUMN_NAME_CONTACT_USERNAME, contact.getUsername());
        contentValues.put(ContactTable.COLUMN_NAME_CONTACT_NAME, contact.getProfile().getName());
        contentValues.put(ContactTable.COLUMN_NAME_CONTACT_EMAIL, contact.getProfile().getEmail());
        db.insert(ContactTable.TABLE_NAME, null, contentValues);
        return true;
    }

    public Integer deleteContact (Contact contact)
    {
        return db.delete(ContactTable.TABLE_NAME,
                ContactTable.COLUMN_NAME_CONTACT_USERNAME + EQUALS + QUESTION_MARK,
                new String[] { contact.getUsername() });
    }

    public boolean updateContact (Contact contact )
    {
        deleteContact(contact);
        insertContact(contact);
        return true;
    }

    public ArrayList<Contact> getAllContacts()
    {
        ArrayList<Contact> contacts = new ArrayList();
        Cursor res =  db.rawQuery( SELECT_ALL + FROM + ContactTable.TABLE_NAME, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String name = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUMN_NAME_CONTACT_NAME));
            String email = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUMN_NAME_CONTACT_EMAIL));
            String id = res.getString(res.getColumnIndexOrThrow(ContactTable.COLUMN_NAME_CONTACT_USERNAME));
            Profile profile = new Profile();
            profile.setNickName(email);
            profile.setName(name);
            Contact c = new Contact(id, profile);
            contacts.add(c);
            res.moveToNext();
        }
        return contacts;
    }

    public boolean insertMessage (ClientMessage message) {
        ContentValues contentValues = new ContentValues();
        ADigitalPerson receiver = (ADigitalPerson)message.getReceivers().get(0);
        contentValues.put(MessagesTable.COLUMN_NAME_SENDER, message.getSender().getUsername());
        contentValues.put(MessagesTable.COLUMN_NAME_RECEIVER, receiver.getUsername());
        contentValues.put(MessagesTable.COLUMN_NAME_TIMESTAMP, message.getTimestamp());
        contentValues.put(MessagesTable.COLUMN_NAME_TYPE, message.getType().toString());
        contentValues.put(MessagesTable.COLUMN_NAME_CONTENT, new Gson().toJson(message.getContent()));
        db.insert(MessagesTable.TABLE_NAME, null, contentValues);
        return true;
    }

    public Integer deleteMessage (long timestamp, String sender, String receiver)
    {
        return db.delete(MessagesTable.TABLE_NAME,
                (MessagesTable.COLUMN_NAME_TIMESTAMP + EQUALS + QUESTION_MARK + AND + MessagesTable.COLUMN_NAME_SENDER + EQUALS + QUESTION_MARK + AND +
                MessagesTable.COLUMN_NAME_RECEIVER + EQUALS + QUESTION_MARK),
                new String[] { String.valueOf(timestamp), sender, receiver });
    }


    public ArrayList<ClientMessage> getAllMessagesFromContacts(List<Contact> contacts)
    {
        throw new UnsupportedOperationException();
        //TODO: finish this method
        /*ArrayList<ClientMessage> messages = new ArrayList();
        Cursor res =  db.rawQuery( SELECT_ALL + FROM + MessagesTable.TABLE_NAME , null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            int messsageId = res.getInt(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_MESSAGE_ID));
            String contact = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_CONTACT_USERNAME));

            messages.add(null);
            res.moveToNext();
        }
        return messages;*/
    }

    public ArrayList<ClientMessage> getAllMessages() {
        ArrayList<ClientMessage> messages = new ArrayList();
        Cursor res =  db.rawQuery( SELECT_ALL + FROM + MessagesTable.TABLE_NAME, null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String content = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_CONTENT));
            String type = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_TYPE));
            long timestamp = res.getLong(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_TIMESTAMP));
            String sender = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_SENDER));
            String receiver = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_RECEIVER));
            Gson gson = new Gson();
            MessageType typeEnum = MessageType.valueOf(type);
            switch(typeEnum) {
                case TEXTMESSAGE:
                    String decodedContent = gson.fromJson(content, String.class);
                    List<ADigitalPerson> receivers = new ArrayList<>();
                    receivers.add(new Contact(receiver, new Profile()));
                    messages.add(new ClientMessage(timestamp, new Contact(sender, new Profile()), receivers, decodedContent, typeEnum));
                    break;
                case EMOTICON:
                    //TODO: decode here
                    break;
                case DRAWING:
                    Drawing decodedDrawing = gson.fromJson(content, Drawing.class);
                    List<ADigitalPerson> drawingReceivers = new ArrayList<>();
                    drawingReceivers.add(new Contact(receiver, new Profile()));
                    messages.add(new ClientMessage(timestamp, new Contact(sender, new Profile()), drawingReceivers, decodedDrawing, typeEnum));
                    break;

            }
            res.moveToNext();
        }
        return messages;
    }

    public ArrayList<ClientMessage> getAllMessagesFromAContact(ADigitalPerson contact) {
        ArrayList<ClientMessage> messages = new ArrayList();
        Cursor res =  db.rawQuery( SELECT_ALL + FROM + MessagesTable.TABLE_NAME + WHERE + MessagesTable.COLUMN_NAME_SENDER
                + EQUALS + QUESTION_MARK + OR + MessagesTable.COLUMN_NAME_RECEIVER +
                EQUALS + QUESTION_MARK, new String[] {contact.getUsername(), contact.getUsername()} );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String content = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_CONTENT));
            String type = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_TYPE));
            long timestamp = res.getLong(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_TIMESTAMP));
            String sender = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_SENDER));
            String receiver = res.getString(res.getColumnIndexOrThrow(MessagesTable.COLUMN_NAME_RECEIVER));
            Gson gson = new Gson();
            MessageType typeEnum = MessageType.valueOf(type);
            switch(typeEnum) {
                case TEXTMESSAGE:
                    String decodedContent = gson.fromJson(content, String.class);
                    List<ADigitalPerson> receivers = new ArrayList<>();
                    receivers.add(new Contact(receiver, new Profile()));
                    messages.add(new ClientMessage(timestamp, new Contact(sender, new Profile()), receivers, decodedContent, typeEnum));
                    break;
                case EMOTICON:
                    //TODO: decode here
                    break;
                case DRAWING:
                    Drawing decodedDrawing = gson.fromJson(content, Drawing.class);
                    List<ADigitalPerson> drawingReceivers = new ArrayList<>();
                    drawingReceivers.add(new Contact(receiver, new Profile()));
                    messages.add(new ClientMessage(timestamp, new Contact(sender, new Profile()), drawingReceivers, decodedDrawing, typeEnum));
                    break;

            }
            res.moveToNext();
        }
        return messages;
    }

    private int numberOfRows(String tableName){
        int numRows = (int) DatabaseUtils.queryNumEntries(db, tableName);
        return numRows;
    }

}
