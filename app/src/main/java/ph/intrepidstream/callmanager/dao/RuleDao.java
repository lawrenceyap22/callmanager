package ph.intrepidstream.callmanager.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.util.Country;

public interface RuleDao {

    List<Rule> retrieveCustomRules(SQLiteDatabase db);

    List<Rule> retrieveRulesByCountry(SQLiteDatabase db, Country country);

    Rule getRuleByName(SQLiteDatabase db, String name);

    long insertRule(SQLiteDatabase db, Rule rule);

    boolean updateRule(SQLiteDatabase db, Rule oldRule, Rule newRule);

    int deleteRule(SQLiteDatabase db, Long id);

}
