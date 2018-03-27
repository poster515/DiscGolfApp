package com.scoresheet.discgolf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Joe Post on 4/20/2017.
 */

public class DatabaseHelperScoringTable  extends SQLiteOpenHelper{

    // Logcat tag
    private static final String LOG = "DBHelperScoringTable";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DiscGolfScoreTable";

    // Table Name
    private static final String SCORING_TABLE= "SingleRoundScoringTable";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_COURSE_TABLE_ID = "course_table_id";
    private static final String KEY_HOLE = "hole";
    private static final String KEY_PAR = "par";
    private static final String KEY_SCORE = "score";
    private static final String KEY_DONE = "done";
    private static final String KEY_NAME = "name";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UNIQUE_COURSE_ID = "unique_id";
    private static final String done = "1";
    private static final String not_done = "0";
    private static final String[] not_done_query = {"0"};
    private static final String[] done_query = {"1"};

    private static final String CREATE_SCORING_TABLE = "CREATE TABLE " + SCORING_TABLE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_COURSE_TABLE_ID + " INTEGER,"
            + KEY_HOLE + " INTEGER,"
            + KEY_PAR + " INTEGER,"
            + KEY_NAME + " VARCHAR(20),"
            + KEY_SCORE + " INTEGER,"
            + KEY_DONE + " VARCHAR(20),"
            + KEY_CREATED_AT + " DATETIME,"
            + KEY_UNIQUE_COURSE_ID + " INTEGER" + ")";

    public DatabaseHelperScoringTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_SCORING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + SCORING_TABLE);

        // create new tables
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){

//        db.execSQL("DROP TABLE IF EXISTS " + SCORING_TABLE);
    }

    // the done(context) method will mark all entries in the table as done, which will just be a bit
    // in the entire database

    public void done(Integer course_id){
        //select all entries in current database
        String selectQuery = "SELECT * FROM " + SCORING_TABLE + " where done = ?";
        //query the entire table for all entries that are not flagged as done yet
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, not_done_query);

        //create new ContentValues
        ContentValues cv = new ContentValues(3);
        //looping through all rows and adding to list
        if(!(c == null)){

            if (c.moveToFirst()) {
                do {
                    //place 'done' in new ContentValue
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    cv.put(KEY_DONE, done);
                    cv.put(KEY_COURSE_TABLE_ID, course_id); //may have to ensure this is incremented by 1
                    cv.put(KEY_CREATED_AT, dateFormat.format(date));

                    //create update query string
                    String updateQuery = "done = ?";

                    //now update the table!!
                    db.update(SCORING_TABLE, cv, updateQuery, not_done_query);

                    //Toast.makeText(context, "Updated Entry!!", Toast.LENGTH_LONG).show();

                } while (c.moveToNext());
            }
            c.close();
        }
    }

    //now we need to make a code snippet that will let us add entries to the
    //SingleRoundScoringTable table.

    public ArrayList<Integer> getSinglePlayerRow(String name, Context context){
        //this method returns a list for a given player which includes only the player's scores
        //TODO: modify to also include course_ID filter
        String[] playername = {name, not_done};
        String selectQuery = "SELECT " + KEY_SCORE + " FROM " + SCORING_TABLE + " where name = ? and done = ?";
        SQLiteDatabase db1 = this.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();

        Cursor c = db1.rawQuery(selectQuery, playername);
        int loop1 = 0;

        if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    list.add(loop1, c.getInt(c.getColumnIndex(KEY_SCORE)));
                    loop1++;
                } while (c.moveToNext());
            c.close();
        }

        return list;
    }
    public boolean noCurrentCourse(){
        String selectQuery = "SELECT * FROM " + SCORING_TABLE + " where done = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, not_done_query);

        boolean no_current_course;
        if(c == null || c.getCount() == 0) {
            no_current_course = true;
        } else { no_current_course = false; c.close(); }

        return no_current_course;
    }
    public Bundle getAllDoneCourses() {
        //this will return the names and dates for all done courses for first fragment
        ArrayList<String> date_list = new ArrayList<>();
        ArrayList<Integer> course_id_list = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + SCORING_TABLE + " where unique_id = ? and done = ?";
        int loop = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Bundle args = new Bundle();
        Cursor c = null;
        do {
            String[] unique_id = {Integer.toString(loop), done};
            //search for the specific unique_id, starting at 0
            c = db.rawQuery(selectQuery, unique_id);

            if(c != null && c.getCount() > 0){
                //this will return all holes in a particular scoring session, so just move
                //the cursor to the first entry
                c.moveToFirst();
                String str_date = c.getString(c.getColumnIndex(KEY_CREATED_AT));
                date_list.add(loop, str_date);
                course_id_list.add(loop, c.getInt(c.getColumnIndex(KEY_COURSE_TABLE_ID)));
            } else { break; }
            loop++;
        } while ( c.getCount() > 0 ); //do while the cursor is not null, otherwise quit
        c.close();
        args.putStringArrayList("date_list", date_list);
        args.putIntegerArrayList("course_id_list", course_id_list);

        //try grabbing all holes listed as being hole 1, this will preclude players from
        //viewing courses without a hole 1 however.
        String[] hole = {"1"};

        return args;
    }
    public ArrayList<Integer> getSingleHoleRow(String name){
        //this method returns a list for a given player which includes only the player's scores
        String[] playername = {name, not_done};
        //TODO: modify to also include course_ID filter
        String selectQuery = "SELECT " + KEY_HOLE + " FROM " + SCORING_TABLE + " where name = ? and done = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();

        Cursor c = db.rawQuery(selectQuery, playername);

        int loop2 = 0;
        if(!(c == null)){

            if (c.moveToFirst()) {
                do {
                    list.add(loop2, c.getInt(c.getColumnIndex(KEY_HOLE)));
                    loop2++;
                } while (c.moveToNext());
            }
            c.close();
        }

        return list;
    }

    public ArrayList<Integer> getSingleParRow(String name){
        //this method returns a list for a given player which includes only the par for each hole
        //TODO: modify to also include course_ID filter
        String selectQuery = "SELECT " + KEY_PAR + " FROM " + SCORING_TABLE + " where name = ? and done = ?";
        String[] playername = {name, not_done};
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();

        Cursor c = db.rawQuery(selectQuery, playername);
        int childcount = c.getCount();

        int loop3 = 0;
        if(c != null){
            if (c.moveToFirst()) {
                do {
                    list.add(loop3, c.getInt(c.getColumnIndex(KEY_PAR)));
                    loop3++;
                } while (c.moveToNext());
            }
        }
        c.close();

        return list;
    }

    public void addRowtoSingleScoringTable(Bundle args) {
        //use this method to add a row to the current SingleRoundScoringTable
        ArrayList<Integer> hole_par_score_array = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
        ArrayList<String> PlayerNames = new ArrayList<>();
        ContentValues values = new ContentValues(7);

        Integer unique_course_id = 0;

        //initialize each array
        hole_par_score_array = args.getIntegerArrayList("hp");
        PlayerNames = args.getStringArrayList("pn");

        //need to get new unique course ID
        if (this.noCurrentCourse()) {  //this means there is no currently open course, and this is the first entry to the
            //new table
            //get the last entry in the table, if any 'done' courses exist
            String selectQuery = "SELECT * FROM " + SCORING_TABLE + " where done = ?";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, done_query);

            if (c != null && c.getCount() > 0) {
                //this means we have a saved course, and we just increment it
                c.moveToLast();
                Integer i = c.getInt(c.getColumnIndex(KEY_UNIQUE_COURSE_ID));
                unique_course_id = i + 1;
            } else {
                //this means there are no currently saved database entries at all.
                unique_course_id = 0;
            }
            c.close();
        } else {
            //there is currently an open course being scored, so grab that unique course ID
            String selectQuery = "SELECT * FROM " + SCORING_TABLE + " where done = ?";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, not_done_query);

            if (c != null && c.getCount() > 0) {
                c.moveToLast();
                Integer i = c.getInt(c.getColumnIndex(KEY_UNIQUE_COURSE_ID));
                unique_course_id = i;
            }
            c.close();
        }

        for (int i = 0; i < PlayerNames.size(); i++) {
            scores.add(i, hole_par_score_array.get(4 + i));
            values.put(KEY_COURSE_TABLE_ID, "1"); //TODO fix this to increment with new table
            values.put(KEY_HOLE, hole_par_score_array.get(1));
            values.put(KEY_PAR, hole_par_score_array.get(2));
            values.put(KEY_NAME, PlayerNames.get(i));
            values.put(KEY_SCORE, scores.get(i));
            String score = Integer.toString(scores.get(i));
            values.put(KEY_DONE, not_done);
            values.put(KEY_UNIQUE_COURSE_ID, unique_course_id);
            //now insert this row into the table
            getWritableDatabase().insert(SCORING_TABLE, KEY_COURSE_TABLE_ID, values);
        }
    }

    public ArrayList<String> getPlayerNamesforUniqueCourse(Integer course_id){
        //this method returns a list for a given player which includes only the par for each hole
        //TODO: modify to also include course_ID filter
        String selectQuery = "SELECT " + KEY_NAME + " FROM "
                                + SCORING_TABLE + " where unique_id = ?";

        String[] ID = {String.valueOf(course_id)};

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> final_list = new ArrayList<>();

        Cursor c = db.rawQuery(selectQuery, ID);

        int loop3 = 0;
        if(c != null) {
            if (c.moveToFirst()) {
                do {
                    list.add(loop3, c.getString(c.getColumnIndex(KEY_NAME)));
                    loop3++;
                } while (c.moveToNext());
            }
            final_list.add(0, list.get(0));
            if (list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    if (!(list.get(0).equals(list.get(i)))) {
                        final_list.add(i, list.get(i));
                    }else {break;}
                }
            } else {
                final_list = list;
            }
        }

        c.close();

        return final_list;
    }
    public ArrayList<Integer> getHolesForUniqueCourse(Integer course_id, String name){
        //this method returns a list for a given player which includes only the par for each hole
        //TODO: modify to also include course_ID filter
        String selectQuery = "SELECT " + KEY_HOLE + " FROM "
                + SCORING_TABLE + " where unique_id = ? and name = ?";

        String[] ID = {String.valueOf(course_id), name};

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();

        Cursor c = db.rawQuery(selectQuery, ID);

        int loop3 = 0;
        if(c != null){
            if (c.moveToFirst()) {
                do {
                    list.add(loop3, c.getInt(c.getColumnIndex(KEY_HOLE)));
                    loop3++;
                } while (c.moveToNext());
            }
        }
        c.close();

        return list;
    }
    public ArrayList<Integer> getParsForUniqueCourse(Integer course_id, String name){
        //this method returns a list for a given player which includes only the par for each hole
        //TODO: modify to also include course_ID filter
        String selectQuery = "SELECT " + KEY_PAR + " FROM "
                + SCORING_TABLE + " where unique_id = ? and name = ?";

        String[] ID = {String.valueOf(course_id), name};

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();

        Cursor c = db.rawQuery(selectQuery, ID);

        int loop3 = 0;
        if(c != null){
            if (c.moveToFirst()) {
                do {
                    list.add(loop3, c.getInt(c.getColumnIndex(KEY_PAR)));
                    loop3++;
                } while (c.moveToNext());
            }
        }
        c.close();

        return list;
    }
    public ArrayList<Integer> getPlayerScoresUniqueCourse(Integer course_id, String name){
        //this method returns a list for a given player which includes only the score for each hole
        //TODO: modify to also include course_ID filter
        String selectQuery = "SELECT " + KEY_SCORE + " FROM "
                + SCORING_TABLE + " where unique_id = ? and name = ?";

        String[] ID = {String.valueOf(course_id), name};

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();

        Cursor c = db.rawQuery(selectQuery, ID);

        int loop3 = 0;
        if(c != null){
            if (c.moveToFirst()) {
                do {
                    list.add(loop3, c.getInt(c.getColumnIndex(KEY_SCORE)));
                    loop3++;
                } while (c.moveToNext());
            }
        }
        c.close();

        return list;
    }
    public ArrayList<Integer> getParsUniqueCourse(Integer course_id, String name){
        String selectQuery = "SELECT " + KEY_PAR + " FROM "
                + SCORING_TABLE + " where unique_id = ? and name = ?";

        String[] ID = {String.valueOf(course_id), name};

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> list = new ArrayList<>();

        Cursor c = db.rawQuery(selectQuery, ID);

        int loop3 = 0;
        if(c != null){
            if (c.moveToFirst()) {
                do {
                    list.add(loop3, c.getInt(c.getColumnIndex(KEY_PAR)));
                    loop3++;
                } while (c.moveToNext());
            }
        }
        c.close();

        return list;
    }
    public ArrayList<String> getAllCurrentNames(){
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> final_list = new ArrayList<>();

        String selectQuery = "SELECT " + KEY_NAME + " FROM "
                + SCORING_TABLE + " where done = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, not_done_query);

        int loop = 0;
        if(c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                list.add(loop, c.getString(c.getColumnIndex(KEY_NAME)));
                loop++;
            } while (c.moveToNext());

            final_list.add(0, list.get(0));

            if (list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    if (!(list.get(0).equals(list.get(i)))) {
                        final_list.add(i, list.get(i));
                    } else {
                        break;
                    }
                }
            }
        }
        return final_list;
    }
    public void editRowtoSingleScoringTable(Bundle args) {
        //use this method to add a row to the current SingleRoundScoringTable
        ArrayList<Integer> hole_par_score_array = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
        ArrayList<String> PlayerNames = new ArrayList<>();
        ContentValues values = new ContentValues(3);

        Integer unique_course_id = 0;

        //initialize each array
        hole_par_score_array = args.getIntegerArrayList("hp");
        PlayerNames = args.getStringArrayList("pn");

        for (int i = 0; i < PlayerNames.size(); i++) {
            scores.add(i, hole_par_score_array.get(4 + i));

            values.put(KEY_NAME, PlayerNames.get(i));
            values.put(KEY_SCORE, scores.get(i));
            values.put(KEY_PAR, hole_par_score_array.get(2));

            //now insert this row into the table
            String updateQuery = "hole = ? and name = ?";
            String[] hole_num = {String.valueOf(hole_par_score_array.get(1)), PlayerNames.get(i)};
            SQLiteDatabase db = this.getReadableDatabase();
            //now update the table!!
            db.update(SCORING_TABLE, values, updateQuery, hole_num);
        }
    }
    public void deleteCurrentTable(){
        //this method returns a list for a given player which includes only the player's scores
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(SCORING_TABLE,"done = ?",not_done_query);
    }
    public Integer getBestIDForSpecificCourseAndName(Integer id, String name) {
        //this method returns a list for a given player which includes only the par for each hole
        //TODO: modify to also include course_ID filter
        String selectQuery = "SELECT * FROM "
                + SCORING_TABLE + " where course_table_id = ? and name = ?";

        String[] ID = {String.valueOf(id), name};

        SQLiteDatabase db = this.getReadableDatabase();
        //list will obtain all unique course IDs for a particular course ID
        ArrayList<Integer> list = new ArrayList<>();
        Cursor c = db.rawQuery(selectQuery, ID);
        ArrayList<Integer> scores = new ArrayList<>();
        ArrayList<Integer> pars = new ArrayList<>();
        Integer best_score_index = 0;
        int loop = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    list.add(loop, c.getInt(c.getColumnIndex(KEY_UNIQUE_COURSE_ID)));
                    loop++;
                } while (c.moveToNext());
            }
            c.close();
            if(list.size() > 1) {
                loop = 0;
                do{
                    if (list.get(loop).equals(list.get(loop + 1))) {
                        list.remove(loop + 1);

                    } else {
                        loop++;
                    }
                } while ((loop + 1) < list.size());
            }


            int best_score = 0;
            for(int i = 0; i < list.size(); i++){
                scores = this.getPlayerScoresUniqueCourse(list.get(i), name);
                pars = this.getParsUniqueCourse(list.get(i), name);
                int this_score = 0;
                for (int k = 0; k < scores.size(); k++) {
                    this_score += (scores.get(k)) - pars.get(k);
                }
                if(i == 0){
                    best_score = this_score;
                    best_score_index = list.get(i);
                } else {
                    if(this_score < best_score){
                        best_score = this_score;
                        best_score_index = list.get(i);
                    }
                }
            }

        }
        return best_score_index;
    }
    public ArrayList<String> getPlayersForSpecificCourse (Integer course_id){
        ArrayList<String> names = new ArrayList<>();
        String selectQuery = "SELECT name FROM "
                + SCORING_TABLE + " where course_table_id = ? and done = ?";

        String[] ID = {String.valueOf(course_id), done};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, ID);
        int loop = 0;

        if(c != null){
            if (c.moveToFirst()) {
                do {
                    names.add(loop, c.getString(c.getColumnIndex(KEY_NAME)));
                    loop++;

                } while (c.moveToNext());
            }
            c.close();
            if(names.size() > 1){
                for(int i = 0; i < names.size() - 1; i++){
                    int j = i + 1;
                    do{
                        if(names.get(i).equalsIgnoreCase(names.get(j))){
                            names.remove(j);
                        } else {
                            j++;
                        }
                    } while(j < names.size());
                }
                if(names.get(names.size() - 2).equalsIgnoreCase(names.get(names.size() - 1))){
                    names.remove(names.size() - 1);
                }
            }
        }
        return names;
    }
}