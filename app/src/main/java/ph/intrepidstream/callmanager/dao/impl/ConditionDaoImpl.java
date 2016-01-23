package ph.intrepidstream.callmanager.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.dao.ConditionDao;
import ph.intrepidstream.callmanager.db.CallManagerDatabaseContract;
import ph.intrepidstream.callmanager.dto.Condition;

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
        String[] columns = {CallManagerDatabaseContract.ConditionEntry._ID, CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID, CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_NUMBER};
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
        contentValues.put(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_NUMBER, condition.getNumber());
        return db.insert(CallManagerDatabaseContract.ConditionEntry.TABLE_NAME, null, contentValues);
    }

    @Override
    public boolean updateConditionsByRule(SQLiteDatabase db, List<Condition> oldConditions, List<Condition> newConditions) {
        boolean hasError = false;

        Condition newCondition;
        Iterator<Condition> newConditionsIterator = newConditions.iterator();
        while (!hasError && newConditionsIterator.hasNext()) {
            newCondition = newConditionsIterator.next();
            if (!oldConditions.contains(newCondition)) {
                hasError = insertCondition(db, newCondition) == -1;
            }
        }

        if (!hasError) {
            Condition oldCondition;
            Iterator<Condition> oldConditionsIterator = oldConditions.iterator();
            while (!hasError && oldConditionsIterator.hasNext()) {
                oldCondition = oldConditionsIterator.next();
                if (!newConditions.contains(oldCondition)) {
                    hasError = deleteCondition(db, oldCondition.getId()) == 0;
                }
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
