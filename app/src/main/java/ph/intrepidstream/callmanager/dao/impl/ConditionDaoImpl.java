package ph.intrepidstream.callmanager.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.dao.ConditionDao;
import ph.intrepidstream.callmanager.db.CallManagerDatabaseContract;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.util.ConditionLookup;

public class ConditionDaoImpl implements ConditionDao {

    private final String TAG = ConditionDao.class.getName();

    private static ConditionDao instance = null;

    public static synchronized ConditionDao getInstance() {
        if (instance == null) {
            instance = new ConditionDaoImpl();
        }
        return instance;
    }

    private ConditionDaoImpl() {
    }

    @Override
    public List<Condition> retrieveConditions(SQLiteDatabase db, Long ruleId) {
        List<Condition> conditions = new ArrayList<>();
        String[] columns = {CallManagerDatabaseContract.ConditionEntry._ID, CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID, CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_LOOKUP, CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_NUMBER};
        String whereClause = CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID + "=?";
        String[] whereClauseArgs = {ruleId.toString()};
        String sortOrder = CallManagerDatabaseContract.ConditionEntry._ID;
        Cursor cursor = db.query(CallManagerDatabaseContract.ConditionEntry.TABLE_NAME, columns, whereClause, whereClauseArgs, null, null, sortOrder);
        Condition condition;

        if (cursor.moveToFirst()) {
            do {
                condition = new Condition();
                condition.setId(cursor.getLong(cursor.getColumnIndex(CallManagerDatabaseContract.ConditionEntry._ID)));
                condition.setRuleId(cursor.getLong(cursor.getColumnIndex(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID)));
                condition.setLookup(ConditionLookup.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_LOOKUP))));
                condition.setNumber(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_NUMBER)));
                conditions.add(condition);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Retrieved Condition: " + condition.toString());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conditions;
    }

    @Override
    public long insertCondition(SQLiteDatabase db, Condition condition) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID, condition.getRuleId());
        contentValues.put(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_LOOKUP, condition.getLookup().name());
        contentValues.put(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_NUMBER, condition.getNumber());
        return db.insert(CallManagerDatabaseContract.ConditionEntry.TABLE_NAME, null, contentValues);
    }

    @Override
    public int updateCondition(SQLiteDatabase db, Condition condition) {
        String whereClause = CallManagerDatabaseContract.ConditionEntry._ID + "=?";
        String[] whereClauseArgs = {condition.getId().toString()};

        ContentValues contentValues = new ContentValues();
        contentValues.put(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_LOOKUP, condition.getLookup().name());
        return db.update(CallManagerDatabaseContract.ConditionEntry.TABLE_NAME, contentValues, whereClause, whereClauseArgs);

    }

    @Override
    public boolean updateConditionsByRule(SQLiteDatabase db, List<Condition> oldConditions, List<Condition> newConditions) {
        boolean hasError = false;

        Map<String, Condition> oldConditionsMap = new HashMap<>(oldConditions.size());
        Map<String, Condition> newConditionsMap = new HashMap<>(newConditions.size());

        for (Condition oldCondition : oldConditions) {
            oldConditionsMap.put(oldCondition.getNumber(), oldCondition);
        }

        for (Condition newCondition : newConditions) {
            newConditionsMap.put(newCondition.getNumber(), newCondition);
        }

        Set<String> oldConditionsNumbers = oldConditionsMap.keySet();
        Set<String> newConditionNumbers = newConditionsMap.keySet();

        Condition newCondition;
        Condition oldCondition;

        Iterator<Map.Entry<String, Condition>> newConditionEntryIterator = newConditionsMap.entrySet().iterator();
        while (!hasError && newConditionEntryIterator.hasNext()) {
            Map.Entry<String, Condition> newConditionEntry = newConditionEntryIterator.next();
            newCondition = newConditionEntry.getValue();
            if (oldConditionsNumbers.contains(newConditionEntry.getKey())) {
                oldCondition = oldConditionsMap.get(newConditionEntry.getKey());
                newCondition.setId(oldCondition.getId());
                if (!newCondition.equals(oldCondition)) {
                    hasError = updateCondition(db, newCondition) == 0;
                }
            } else {
                hasError = insertCondition(db, newCondition) == -1;
            }
        }

        Iterator<Map.Entry<String, Condition>> oldConditionEntryIterator = oldConditionsMap.entrySet().iterator();
        while (!hasError && oldConditionEntryIterator.hasNext()) {
            Map.Entry<String, Condition> oldConditionEntry = oldConditionEntryIterator.next();
            if (!newConditionNumbers.contains(oldConditionEntry.getKey())) {
                hasError = deleteCondition(db, oldConditionEntry.getValue().getId()) == 0;
            }
        }

        return hasError;
    }

    @Override
    public int deleteCondition(SQLiteDatabase db, Long id) {
        String conditionWhereClause = CallManagerDatabaseContract.ConditionEntry._ID + "=?";
        String[] whereClauseArgs = {id.toString()};
        return db.delete(CallManagerDatabaseContract.ConditionEntry.TABLE_NAME, conditionWhereClause, whereClauseArgs);
    }

    @Override
    public int deleteConditionsByRule(SQLiteDatabase db, Long ruleId) {
        String conditionWhereClause = CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID + "=?";
        String[] whereClauseArgs = {ruleId.toString()};
        return db.delete(CallManagerDatabaseContract.ConditionEntry.TABLE_NAME, conditionWhereClause, whereClauseArgs);
    }
}
