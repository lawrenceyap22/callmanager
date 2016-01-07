package ph.intrepidstream.callmanager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Justin on 1/1/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    //Cursor object always expects that the primary key column has the name _id or it will throw an exception. -Justin :)
    public static final String DATABASE_NAME = "CallManager.db";
    public static final String RULE_TABLE_NAME = "rule";
    public static final String RULE_COLUMN_ID = "rule_id";
    public static final String RULE_COLUMN_NAME = "rule_name";
    public static final String RULE_COLUMN_STATE = "rule_state"; //0-off, 1-warn, 2-block
    public static final String CONDITION_TABLE_NAME = "condition";
    public static final String CONDITION_COLUMN_ID = "condition_id";
    public static final String CONDITION_COLUMN_RULE_ID = "rule_id";
    public static final String CONDITION_COLUMN_CODE = "condition_code"; //0-starts with, 1-equals, 2-not starts with, 3-not equals
    public static final String CONDITION_COLUMN_NUMBER = "condition_number";

    private final String LOG_TAG = "testing";
    private final String GLOBE_PREFIXES = "0817,0905,0906,0915,0916,0917,0926,0927,0935,0936,0937,0945,0975,0976,0977,0994,0995,0996,0997";
    private final String SMART_PREFIXES = "0813,0907,0908,0909,0910,0911,0912,0913,0914,0918,0919,0920,0921,0928,0929,0930,0938,0939,0946,0947,0948,0949,0950,0970,0981,0989,0998,0999";
    private final String SUN_PREFIXES = "0922,0923,0924,0925,0932,0933,0934,0942,0943";
    private final String EXTELCOM_PREFIXES = "0973,0974";
    private final String NEXTMOBILE_PREFIXES = "0978,0979";
    private final String ROAMING_PREFIXES = "00";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Will execute only on first run after installation of app

        //Create tables
        db.execSQL(
                "create table if not exists " + RULE_TABLE_NAME + "("
                        + RULE_COLUMN_ID + " integer primary key autoincrement,"
                        + RULE_COLUMN_NAME + " text,"
                        + RULE_COLUMN_STATE + " integer,unique("
                        + RULE_COLUMN_NAME + "))"
        );
        db.execSQL(
                "create table if not exists " + CONDITION_TABLE_NAME + "("
                        + CONDITION_COLUMN_ID + " integer primary key autoincrement,"
                        + CONDITION_COLUMN_CODE + " integer,"
                        + CONDITION_COLUMN_NUMBER + " text,"
                        + CONDITION_COLUMN_RULE_ID + " integer,"
                        + "foreign key(" + CONDITION_COLUMN_RULE_ID + ") references " + RULE_TABLE_NAME + "(" + RULE_COLUMN_ID + "))"
        );

        //Insert developer-defined rules
        List<Integer> condition_codes = new ArrayList<Integer>();
        List<String> condition_numbers = new ArrayList<String>();
        condition_codes.add(0);
        condition_numbers.add(GLOBE_PREFIXES);
        insertRuleAndCondition(db, "Globe / Touch Mobile", 0, condition_codes, condition_numbers);
        condition_numbers = new ArrayList<String>();
        condition_numbers.add(SMART_PREFIXES);
        insertRuleAndCondition(db, "Smart / Talk n Text", 0, condition_codes, condition_numbers);
        condition_numbers = new ArrayList<String>();
        condition_numbers.add(SUN_PREFIXES);
        insertRuleAndCondition(db, "Sun Cellular", 0, condition_codes, condition_numbers);
        condition_numbers = new ArrayList<String>();
        condition_numbers.add(EXTELCOM_PREFIXES);
        insertRuleAndCondition(db, "Extelcom", 0, condition_codes, condition_numbers);
        condition_numbers = new ArrayList<String>();
        condition_numbers.add(NEXTMOBILE_PREFIXES);
        insertRuleAndCondition(db, "Next Mobile", 0, condition_codes, condition_numbers);
        condition_numbers = new ArrayList<String>();
        condition_numbers.add(ROAMING_PREFIXES);
        insertRuleAndCondition(db, "Roaming", 0, condition_codes, condition_numbers);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }

    public boolean insertCondition(SQLiteDatabase db, List<Integer> condition_codes, List<String> condition_numbers, int rule_id){
        for(int i=0; i<condition_codes.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONDITION_COLUMN_RULE_ID,rule_id);
            contentValues.put(CONDITION_COLUMN_CODE, condition_codes.get(i));
            contentValues.put(CONDITION_COLUMN_NUMBER, condition_numbers.get(i));
            if(db.insert(CONDITION_TABLE_NAME, null, contentValues) == -1){
                Log.d(LOG_TAG,"Cannot insert into table " + CONDITION_TABLE_NAME);
                return false;
            }
        }
        return true;
    }
    public boolean insertRuleAndCondition  (SQLiteDatabase db, String rule_name, int rule_state, List<Integer> condition_codes, List<String> condition_numbers)
    {
        int nextId;
        if((nextId = getNextAutoincrementID(db,RULE_TABLE_NAME)) == -1){
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(RULE_COLUMN_NAME, rule_name);
        contentValues.put(RULE_COLUMN_STATE, rule_state);
        if(db.insert(RULE_TABLE_NAME, null, contentValues) == -1){
            Log.d(LOG_TAG,"Cannot insert into table " + RULE_TABLE_NAME);
            return false;
        }

        for(int i=0; i<condition_codes.size(); i++){
            contentValues = new ContentValues();
            contentValues.put(CONDITION_COLUMN_RULE_ID,nextId);
            contentValues.put(CONDITION_COLUMN_CODE, condition_codes.get(i));
            contentValues.put(CONDITION_COLUMN_NUMBER, condition_numbers.get(i));
            if(db.insert(CONDITION_TABLE_NAME, null, contentValues) == -1){
                Log.d(LOG_TAG,"Cannot insert into table " + CONDITION_TABLE_NAME);
                return false;
            }
        }

        return true;
    }
    public void updateRule(SQLiteDatabase db, String rule_name, int rule_state, String old_rule_name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(RULE_COLUMN_NAME, rule_name);
        contentValues.put(RULE_COLUMN_STATE, rule_state);
        db.update(RULE_TABLE_NAME, contentValues, RULE_COLUMN_NAME + "=?", new String[]{old_rule_name});
    }
    public void updateCondition(SQLiteDatabase db, int condition_code, String condition_number, int old_condition_code, int rule_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONDITION_COLUMN_CODE, condition_code);
        contentValues.put(CONDITION_COLUMN_NUMBER, condition_number);
        db.update(CONDITION_TABLE_NAME, contentValues, CONDITION_COLUMN_CODE + "=? and " + CONDITION_COLUMN_RULE_ID + "=?", new String[]{String.valueOf(old_condition_code), String.valueOf(rule_id)});
    }
    public void deleteRule(SQLiteDatabase db, String rule_name){
        db.delete(RULE_TABLE_NAME, RULE_COLUMN_NAME + "=?", new String[]{rule_name});
    }
    public void deleteCondition(SQLiteDatabase db, String rule_name, int condition_code){
        db.delete(CONDITION_TABLE_NAME,CONDITION_COLUMN_CODE + "=?" + " and " + CONDITION_COLUMN_RULE_ID + "=?", new String [] {String.valueOf(condition_code),String.valueOf(getRuleID(db,rule_name))});
    }
    public int getRuleID(SQLiteDatabase db, String rule_name){
        int rule_id=1;
        String query = "select rule_id from " + RULE_TABLE_NAME + " where rule_name = ?";
        Cursor cursor = db.rawQuery(query, new String [] {rule_name});
        if (cursor.moveToFirst()){
            rule_id = cursor.getInt(cursor.getColumnIndex("rule_id"));
        }
        return rule_id;
    }
    public int getNextAutoincrementID(SQLiteDatabase db, String table_name){
        /*
          SQLITE_SEQUENCE system table is created automatically after creating any table with autoincrement primary key.
          It has 2 useful columns, name (table name) and seq (the last autoincrement).
          -Justin :)
         */
        int nextId = -1;

        String query = "select seq from sqlite_sequence where name = ?";
        Cursor cursor = db.rawQuery(query, new String [] {table_name});
        if (cursor.moveToFirst()){
            nextId = cursor.getInt(cursor.getColumnIndex("seq")) + 1;
        }
        else{
            //No entries in table yet
            return 1;
        }
        cursor.close();
        return nextId;
    }
    public Cursor getRules(SQLiteDatabase db){
        String query = "select * from " + RULE_TABLE_NAME;
        Cursor cursor =  db.rawQuery(query, null);
        return cursor;
    }
    public Cursor getConditions(SQLiteDatabase db, int rule_id){
        String query = "select * from " + CONDITION_TABLE_NAME + " where rule_id = ?";
        Cursor cursor =  db.rawQuery(query, new String [] {String.valueOf(rule_id)});
        return cursor;
    }
    public void printData(SQLiteDatabase db){
        Cursor cursor =  db.rawQuery("select * from " + RULE_TABLE_NAME + " natural join " + CONDITION_TABLE_NAME, null);
        if (cursor.moveToFirst()){
            do{
                int rule_id = cursor.getInt(cursor.getColumnIndex(RULE_COLUMN_ID));
                String rule_name = cursor.getString(cursor.getColumnIndex(RULE_COLUMN_NAME));
                int rule_state = cursor.getInt(cursor.getColumnIndex(RULE_COLUMN_STATE));
                int condition_code = cursor.getInt(cursor.getColumnIndex(CONDITION_COLUMN_CODE));
                String condition_number = cursor.getString(cursor.getColumnIndex(CONDITION_COLUMN_NUMBER));
                Log.d(LOG_TAG, "rule_id: " + rule_id + ",rule_name: " + rule_name + ",rule_state: " + rule_state + ",condition_code: " + condition_code + ",condition_number: " + condition_number);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
