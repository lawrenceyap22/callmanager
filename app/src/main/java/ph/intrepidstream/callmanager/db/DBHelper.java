package ph.intrepidstream.callmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.util.ConditionLookup;
import ph.intrepidstream.callmanager.util.RuleState;

import static ph.intrepidstream.callmanager.db.CallManagerDatabaseContract.ConditionEntry;
import static ph.intrepidstream.callmanager.db.CallManagerDatabaseContract.RuleEntry;

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = DBHelper.class.getName();

    private Context context;

    public DBHelper(Context context) {
        super(context, CallManagerDatabaseContract.DATABASE_NAME, null, CallManagerDatabaseContract.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CallManagerDatabaseContract.RuleEntry.CREATE_TABLE);
        db.execSQL(CallManagerDatabaseContract.ConditionEntry.CREATE_TABLE);
        initializeDefaultData(db);
    }

    private void initializeDefaultData(SQLiteDatabase db) {
        ContentValues contentValues;
        Long newId;
        boolean hasError;
        int numberCtr;
        String[] numbers;

        Map<String, String[]> defaultData = new LinkedHashMap<>(6);
        defaultData.put(context.getString(R.string.globe_operator), context.getString(R.string.globe_prefixes).split(","));
        defaultData.put(context.getString(R.string.smart_operator), context.getString(R.string.smart_prefixes).split(","));
        defaultData.put(context.getString(R.string.sun_operator), context.getString(R.string.sun_prefixes).split(","));
        defaultData.put(context.getString(R.string.extelcom_operator), context.getString(R.string.extelcom_prefixes).split(","));
        defaultData.put(context.getString(R.string.next_mobile_operator), context.getString(R.string.next_mobile_prefixes).split(","));
        defaultData.put(context.getString(R.string.roaming_numbers), context.getString(R.string.roaming_prefixes).split(","));

        for (Map.Entry<String, String[]> defaultDataEntry : defaultData.entrySet()) {
            db.beginTransaction();
            contentValues = new ContentValues();
            contentValues.put(RuleEntry.COLUMN_NAME_NAME, defaultDataEntry.getKey());
            contentValues.put(RuleEntry.COLUMN_NAME_STATE, RuleState.OFF.toString());
            contentValues.put(RuleEntry.COLUMN_NAME_APP_GENERATED, true);

            newId = db.insert(RuleEntry.TABLE_NAME, null, contentValues);
            if (newId != -1) {
                hasError = false;
                numberCtr = 0;
                numbers = defaultDataEntry.getValue();
                while (!hasError && numberCtr < numbers.length) {
                    contentValues = new ContentValues();
                    contentValues.put(ConditionEntry.COLUMN_NAME_RULE_ID, newId);
                    contentValues.put(ConditionEntry.COLUMN_NAME_LOOKUP, ConditionLookup.STARTS_WITH.toString());
                    contentValues.put(ConditionEntry.COLUMN_NAME_NUMBER, numbers[numberCtr]);

                    if (db.insert(ConditionEntry.TABLE_NAME, null, contentValues) == -1) {
                        hasError = true;
                    }
                    numberCtr++;
                }

                if (!hasError) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Default data " + defaultDataEntry.getKey() + " inserted successfully.");
                    }
                    db.setTransactionSuccessful();
                } else {
                    Log.e(TAG, "One record failed to insert into table " + ConditionEntry.TABLE_NAME);
                }
                db.endTransaction();
            } else {
                db.endTransaction();
                Log.e(TAG, "Cannot insert " + defaultDataEntry.getKey() + "  into table " + RuleEntry.TABLE_NAME);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Check version and run update statement of table
        onCreate(db);
    }

}
