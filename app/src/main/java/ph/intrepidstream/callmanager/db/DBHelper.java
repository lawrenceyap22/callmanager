package ph.intrepidstream.callmanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.dao.RuleDao;
import ph.intrepidstream.callmanager.dao.impl.RuleDaoImpl;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.util.Country;

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = DBHelper.class.getName();

    private static DBHelper instance = null;
    private Context context;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
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
        initializePhilippinesData(db);
        initializeIndonesiaData(db);
    }

    private void initializePhilippinesData(SQLiteDatabase db) {
        Map<String, String[]> defaultData = new LinkedHashMap<>(6);
        defaultData.put(context.getString(R.string.globe_operator), context.getString(R.string.globe_prefixes).split(","));
        defaultData.put(context.getString(R.string.smart_operator), context.getString(R.string.smart_prefixes).split(","));
        defaultData.put(context.getString(R.string.sun_operator), context.getString(R.string.sun_prefixes).split(","));
        defaultData.put(context.getString(R.string.extelcom_operator), context.getString(R.string.extelcom_prefixes).split(","));
        defaultData.put(context.getString(R.string.next_mobile_operator), context.getString(R.string.next_mobile_prefixes).split(","));
        defaultData.put(context.getString(R.string.roaming_numbers), context.getString(R.string.roaming_prefixes).split(","));

        insertData(db, defaultData, Country.PHILIPPINES);
    }

    private void initializeIndonesiaData(SQLiteDatabase db) {
        Map<String, String[]> defaultData = new LinkedHashMap<>(6);
        defaultData.put(context.getString(R.string.telekomunikasi_selular_operator), context.getString(R.string.telekomunikasi_selular_prefixes).split(","));
        defaultData.put(context.getString(R.string.indosat_operator), context.getString(R.string.indosat_prefixes).split(","));
        defaultData.put(context.getString(R.string.xl_axiata_operator), context.getString(R.string.xl_axiata_prefixes).split(","));
        defaultData.put(context.getString(R.string.sampoerna_telekomunikasi_indonesia_operator), context.getString(R.string.sampoerna_telekomunikasi_indonesia_prefixes).split(","));
        defaultData.put(context.getString(R.string.axis_telekom_indonesia_operator), context.getString(R.string.axis_telekom_indonesia_prefixes).split(","));
        defaultData.put(context.getString(R.string.smartfren_telecom_operator), context.getString(R.string.smartfren_telecom_prefixes).split(","));
        defaultData.put(context.getString(R.string.hutchison_3_indonesia_operator), context.getString(R.string.hutchison_3_indonesia_prefixes).split(","));

        insertData(db, defaultData, Country.INDONESIA);
    }

    private void insertData(SQLiteDatabase db, Map<String, String[]> data, Country country) {
        RuleDao ruleDao = RuleDaoImpl.getInstance();
        Rule rule;
        Condition condition;
        List<Condition> conditions;
        String[] numbers;

        for (Map.Entry<String, String[]> dataEntry : data.entrySet()) {
            rule = new Rule();
            numbers = dataEntry.getValue();
            conditions = new ArrayList<>(numbers.length);
            for (String number : numbers) {
                condition = new Condition();
                condition.setNumber(number);
                conditions.add(condition);
            }
            rule.setName(dataEntry.getKey());
            rule.setIsAppGenerated(true);
            rule.setCountry(country);
            rule.setConditions(conditions);

            db.beginTransaction();
            if (ruleDao.insertRule(db, rule) != -1) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, rule.getName() + " inserted successfully.");
                }
                db.setTransactionSuccessful();
            }
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Check version and run update statement of table
        onCreate(db);
    }

}
