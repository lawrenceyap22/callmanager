package ph.intrepidstream.callmanager.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import ph.intrepidstream.callmanager.dto.Rule;

public interface RuleDao {

    List<Rule> retrieveRules(SQLiteDatabase db);

    Rule getRuleByName(SQLiteDatabase db, String name);

    long insertRule(SQLiteDatabase db, Rule rule);

    boolean updateRule(SQLiteDatabase db, Rule oldRule, Rule newRule);

    int deleteRule(SQLiteDatabase db, Long id);

}
