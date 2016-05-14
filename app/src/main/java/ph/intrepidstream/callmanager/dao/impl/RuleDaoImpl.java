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
import ph.intrepidstream.callmanager.dao.RuleDao;
import ph.intrepidstream.callmanager.db.CallManagerDatabaseContract;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.util.Country;
import ph.intrepidstream.callmanager.util.RuleState;

public class RuleDaoImpl implements RuleDao {

    private final String TAG = RuleDao.class.getName();

    private static RuleDao instance = null;
    private ConditionDao conditionDao;

    public static synchronized RuleDao getInstance() {
        if (instance == null) {
            instance = new RuleDaoImpl(ConditionDaoImpl.getInstance());
        }
        return instance;
    }

    private RuleDaoImpl(ConditionDao conditionDao) {
        this.conditionDao = conditionDao;
    }

    @Override
    public List<Rule> retrieveCustomRules(SQLiteDatabase db) {
        List<Rule> rules = new ArrayList<>();
        String[] columns = {CallManagerDatabaseContract.RuleEntry._ID, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_COUNTRY};
        String sortOrder = CallManagerDatabaseContract.RuleEntry._ID;
        String selection = CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED + "=?";
        String[] selectionArgs = {"0"};
        Cursor cursor = db.query(CallManagerDatabaseContract.RuleEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, sortOrder);
        Rule rule;

        if (cursor.moveToFirst()) {
            do {
                rule = new Rule();
                rule.setId(cursor.getLong(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry._ID)));
                rule.setName(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME)));
                rule.setState(RuleState.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE))));
                rule.setIsAppGenerated(cursor.getInt(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED)) != 0);
                rule.setCountry(Country.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_COUNTRY))));
                rule.setConditions(conditionDao.retrieveConditions(db, rule.getId()));
                rules.add(rule);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Retrieved Rule: " + rule.toString());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rules;
    }

    @Override
    public List<Rule> retrieveRulesByCountry(SQLiteDatabase db, Country country) {
        List<Rule> rules = new ArrayList<>();
        String[] columns = {CallManagerDatabaseContract.RuleEntry._ID, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_COUNTRY};
        String sortOrder = CallManagerDatabaseContract.RuleEntry._ID;
        String selection = CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_COUNTRY + "=?";
        String[] selectionArgs = {country.name()};
        Cursor cursor = db.query(CallManagerDatabaseContract.RuleEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, sortOrder);
        Rule rule;

        if (cursor.moveToFirst()) {
            do {
                rule = new Rule();
                rule.setId(cursor.getLong(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry._ID)));
                rule.setName(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME)));
                rule.setState(RuleState.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE))));
                rule.setIsAppGenerated(cursor.getInt(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED)) != 0);
                rule.setCountry(Country.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_COUNTRY))));
                rule.setConditions(conditionDao.retrieveConditions(db, rule.getId()));
                rules.add(rule);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Retrieved Rule: " + rule.toString());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rules;
    }

    @Override
    public Rule getRuleByName(SQLiteDatabase db, String name) {
        Rule rule = null;
        String[] columns = {CallManagerDatabaseContract.RuleEntry._ID, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_COUNTRY};
        String selection = CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME + "=?";
        String[] selectionArgs = {name};
        Cursor cursor = db.query(CallManagerDatabaseContract.RuleEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            rule = new Rule();
            rule.setId(cursor.getLong(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry._ID)));
            rule.setName(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME)));
            rule.setState(RuleState.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE))));
            rule.setIsAppGenerated(cursor.getInt(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED)) != 0);
            rule.setCountry(Country.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_COUNTRY))));
            rule.setConditions(conditionDao.retrieveConditions(db, rule.getId()));
        }
        cursor.close();
        return rule;
    }

    @Override
    public long insertRule(SQLiteDatabase db, Rule rule) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME, rule.getName());
        contentValues.put(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE, RuleState.OFF.name());
        contentValues.put(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED, rule.isAppGenerated());
        contentValues.put(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_COUNTRY, rule.getCountry().name());
        Long newId = db.insert(CallManagerDatabaseContract.RuleEntry.TABLE_NAME, null, contentValues);
        if (newId != -1) {
            boolean hasError = false;
            Condition condition;
            Iterator<Condition> conditionIterator = rule.getConditions().iterator();
            while (!hasError && conditionIterator.hasNext()) {
                condition = conditionIterator.next();
                condition.setRuleId(newId);
                hasError = conditionDao.insertCondition(db, condition) == -1;
            }

            if (hasError) {
                newId = -1L;
            }
        }
        return newId;
    }

    @Override
    public boolean updateRule(SQLiteDatabase db, Rule oldRule, Rule newRule) {
        boolean hasError = false;
        ContentValues contentValues = new ContentValues();
        if (!newRule.getName().equals(oldRule.getName())) {
            contentValues.put(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME, newRule.getName());
        }

        if (newRule.getState() != oldRule.getState()) {
            contentValues.put(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE, newRule.getState().name());
        }

        if (contentValues.size() > 0) {
            String whereClause = CallManagerDatabaseContract.RuleEntry._ID + "=?";
            String[] whereClauseArgs = new String[]{oldRule.getId().toString()};

            hasError = (db.update(CallManagerDatabaseContract.RuleEntry.TABLE_NAME, contentValues, whereClause, whereClauseArgs) == 0);
        }

        if (!hasError) {
            hasError = conditionDao.updateConditionsByRule(db, oldRule.getConditions(), newRule.getConditions());
        }
        return !hasError;
    }

    @Override
    public int deleteRule(SQLiteDatabase db, Long id) {
        String whereClause = CallManagerDatabaseContract.RuleEntry._ID + "=?";
        String[] whereClauseArgs = {id.toString()};
        int deletedRule;

        if ((deletedRule = conditionDao.deleteConditionsByRule(db, id)) > 0) {
            if (db.delete(CallManagerDatabaseContract.RuleEntry.TABLE_NAME, whereClause, whereClauseArgs) == 0) {
                deletedRule = 0;
            }
        }
        return deletedRule;
    }

}
