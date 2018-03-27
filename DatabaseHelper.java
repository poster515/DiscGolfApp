package com.scoresheet.discgolf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Joe Post on 4/12/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DiscGolfCourseTable";

    // Table Names
    private static final String COURSE_TABLE = "DiscGolfCourses";

    // Common column names
    private static final String KEY_ID = "id";

    private static final String KEY_COURSE_NAME = "course_name";
    private static final String KEY_COURSE_ID = "course_id";

    // Table Create Statements
    // Course table create statement, needs to be private, only one of these tables
    private static final String CREATE_COURSE_TABLE = "CREATE TABLE "
            + COURSE_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_COURSE_NAME + " VARCHAR(64),"
            + KEY_COURSE_ID + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_COURSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
        // create new tables
        onCreate(db);
    }

    /*
    * get single course information
    */
    public ArrayList<String> getAllDoneCoursesNames(ArrayList<Integer> ids) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        ArrayList<String> names = new ArrayList<>();

        for(int i = 0; i < ids.size(); i++){
            String selectQuery = "SELECT * FROM " + COURSE_TABLE + " WHERE "
                    + KEY_COURSE_ID + " = ?";
            String[] id_query = {Integer.toString(ids.get(i))};

            c = db.rawQuery(selectQuery, id_query);
            if(c != null && c.getCount() > 0) {
                c.moveToFirst();
                names.add(i, c.getString(c.getColumnIndex(KEY_COURSE_NAME)));
            }
        }

        return names;
    }

    /*
 * getting all courses
 * */
    public ArrayList<String> getAllCoursesNames() {

        ArrayList<String> DGCs = new ArrayList<String>();
        //just search for all names from the database
        String selectQuery = "SELECT * FROM " + COURSE_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        int loop = 0;
        // looping through all rows and adding to list
        if(c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    //add each name to the
                    DGCs.add(loop, c.getString(c.getColumnIndex(KEY_COURSE_NAME)));
                    loop++;
                } while (c.moveToNext());
            }
            c.close();
        }

        return DGCs;
    }

    //now we need to make a code snippet that will let us add entries to the
    //course table.

    public void addNewCourseName(String course_name) {
        //use this method to add a row to the current SingleRoundScoringTable

        ContentValues values = new ContentValues(2);

        String selectQuery = "SELECT * FROM " + COURSE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        Integer course_id = 0;

        if(c == null || c.getCount() == 0){
            course_id = 0;
        } else {
            c.moveToLast();
            course_id = c.getInt(c.getColumnIndex(KEY_COURSE_ID)) + 1;
        }
        c.close();
        //insert into table
        values.put(KEY_COURSE_NAME, course_name);
        values.put(KEY_COURSE_ID, course_id);

        db.insert(COURSE_TABLE, KEY_COURSE_NAME, values);
    }
    public String getCourseName(Integer id) {

        ArrayList<String> DGCs = new ArrayList<String>();
        //just search for all names from the database
        String selectQuery = "SELECT * FROM " + COURSE_TABLE + " where course_id = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        String[] ID = {String.valueOf(id)};
        Cursor c = db.rawQuery(selectQuery, ID);
        String name = null;
        int loop = 0;
        // looping through all rows and adding to list
        if(c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    //add each name to the
                    name = c.getString(c.getColumnIndex(KEY_COURSE_NAME));
                } while (c.moveToNext());
            }
            c.close();
        }

        return name;
    }
}