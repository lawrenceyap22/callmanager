package ph.intrepidstream.callmanager.db;

import android.provider.BaseColumns;

public final class CallManagerDatabaseContract {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "callmanager.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    private CallManagerDatabaseContract() {
    }

    //Cursor object always expects that the primary key column has _id or it will throw an exception ( _id is auto implemented by BaseColumns)
    public static abstract class RuleEntry implements BaseColumns {
        public static final String TABLE_NAME = "rule";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATE = "state"; //off, warn, block
        public static final String COLUMN_NAME_APP_GENERATED = "app_generated";
        public static final String COLUMN_NAME_COUNTRY = "country";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
                + COLUMN_NAME_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP
                + COLUMN_NAME_STATE + TEXT_TYPE + NOT_NULL + COMMA_SEP
                + COLUMN_NAME_APP_GENERATED + INTEGER_TYPE + NOT_NULL + COMMA_SEP
                + COLUMN_NAME_COUNTRY + TEXT_TYPE + NOT_NULL + COMMA_SEP
                + "UNIQUE( " + COLUMN_NAME_NAME + " ))";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ConditionEntry implements BaseColumns {
        public static final String TABLE_NAME = "condition";
        public static final String COLUMN_NAME_RULE_ID = "rule_id";
        public static final String COLUMN_NAME_NUMBER = "number";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
                + COLUMN_NAME_RULE_ID + INTEGER_TYPE + COMMA_SEP
                + COLUMN_NAME_NUMBER + TEXT_TYPE + NOT_NULL + COMMA_SEP
                + "FOREIGN KEY(" + COLUMN_NAME_RULE_ID + ") REFERENCES " + RuleEntry.TABLE_NAME + "(" + RuleEntry._ID + "))";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
