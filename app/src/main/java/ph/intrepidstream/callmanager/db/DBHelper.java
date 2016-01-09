package ph.intrepidstream.callmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.util.ConditionLookup;
import ph.intrepidstream.callmanager.util.RuleState;

import static ph.intrepidstream.callmanager.db.CallManagerDatabaseContract.ConditionEntry;
import static ph.intrepidstream.callmanager.db.CallManagerDatabaseContract.RuleEntry;

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = DBHelper.class.getName();
    private final String[] GLOBE_PREFIXES = {"0817", "0905", "0906", "0915", "0916", "0917", "0926", "0927", "0935", "0936", "0937", "0945", "0975", "0976", "0977", "0994", "0995", "0996", "0997"};
    private final String[] SMART_PREFIXES = {"0813", "0907", "0908", "0909", "0910", "0911", "0912", "0913", "0914", "0918", "0919", "0920", "0921", "0928", "0929", "0930", "0938", "0939", "0946", "0947", "0948", "0949", "0950", "0970", "0981", "0989", "0998", "0999"};
    private final String[] SUN_PREFIXES = {"0922", "0923", "0924", "0925", "0932", "0933", "0934", "0942", "0943"};
    private final String[] EXTELCOM_PREFIXES = {"0973", "0974"};
    private final String[] NEXTMOBILE_PREFIXES = {"0978", "0979"};
    private final String[] ROAMING_PREFIXES = {"00"};

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
        Iterator<String> numberIterator;

        Map<String, List<String>> defaultData = new LinkedHashMap<>(6);
        defaultData.put(context.getString(R.string.globe_operator), Arrays.asList(GLOBE_PREFIXES));
        defaultData.put(context.getString(R.string.smart_operator), Arrays.asList(SMART_PREFIXES));
        defaultData.put(context.getString(R.string.sun_operator), Arrays.asList(SUN_PREFIXES));
        defaultData.put(context.getString(R.string.extelcom_operator), Arrays.asList(EXTELCOM_PREFIXES));
        defaultData.put(context.getString(R.string.next_mobile_operator), Arrays.asList(NEXTMOBILE_PREFIXES));
        defaultData.put(context.getString(R.string.roaming_numbers), Arrays.asList(ROAMING_PREFIXES));

        for (Map.Entry<String, List<String>> defaultDataEntry : defaultData.entrySet()) {
            db.beginTransaction();
            contentValues = new ContentValues();
            contentValues.put(RuleEntry.COLUMN_NAME_NAME, defaultDataEntry.getKey());
            contentValues.put(RuleEntry.COLUMN_NAME_STATE, RuleState.OFF.toString());
            contentValues.put(RuleEntry.COLUMN_NAME_APP_GENERATED, true);

            newId = db.insert(RuleEntry.TABLE_NAME, null, contentValues);
            if (newId != -1) {
                hasError = false;
                numberIterator = defaultDataEntry.getValue().iterator();
                while (!hasError && numberIterator.hasNext()) {
                    contentValues = new ContentValues();
                    contentValues.put(ConditionEntry.COLUMN_NAME_RULE_ID, newId);
                    contentValues.put(ConditionEntry.COLUMN_NAME_LOOKUP, ConditionLookup.STARTS_WITH.toString());
                    contentValues.put(ConditionEntry.COLUMN_NAME_NUMBER, numberIterator.next());

                    if (db.insert(ConditionEntry.TABLE_NAME, null, contentValues) == -1) {
                        hasError = true;
                    }
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
