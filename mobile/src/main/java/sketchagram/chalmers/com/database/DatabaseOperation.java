package sketchagram.chalmers.com.database;

/**
 * Created by alex on 2015-02-24.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import sketchagram.chalmers.com.database.TableData.TableInfo;

public class DatabaseOperation extends SQLiteOpenHelper {

    public static final int database_version = 1;
    public String CREATE_QUERY = "CREATE_TABLE" + TableInfo.Table_Name + "("+ TableInfo.User_Name +"TEXT," + TableInfo.User_pass +"TEXT);" ;

    public DatabaseOperation (Context context){
        super(context,TableInfo.Database_Name,null,database_version);

        Log.d("Database operations", "Database created ");


    }


    public void onCreate(SQLiteDatabase sdb)
    {

        sdb.execSQL(CREATE_QUERY);
        Log.d("Database operations", "Table Created");
    }

    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2)
    {

    }

    public void putInformation(DatabaseOperation dop, String name, String pass)
    {

        SQLiteDatabase sdb = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableInfo.User_pass, pass);
        cv.put(TableInfo.User_Name, name);
        long k = sdb.insert(TableInfo.Table_Name,null,cv);
        Log.d("Database operations", "Row inserted ");

    }



}
