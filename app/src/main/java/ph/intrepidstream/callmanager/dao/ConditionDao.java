package ph.intrepidstream.callmanager.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import ph.intrepidstream.callmanager.dto.Condition;

public interface ConditionDao {

    List<Condition> retrieveConditions(SQLiteDatabase db, Long ruleId);

    long insertCondition(SQLiteDatabase db, Condition condition);

    boolean updateConditionsByRule(SQLiteDatabase db, List<Condition> oldConditions, List<Condition> newConditions);

    int deleteCondition(SQLiteDatabase db, Long id);

    int deleteConditionsByRule(SQLiteDatabase db, Long ruleId);
}
