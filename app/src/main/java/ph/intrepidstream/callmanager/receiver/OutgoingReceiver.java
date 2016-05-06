package ph.intrepidstream.callmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import java.util.Iterator;
import java.util.List;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.dao.RuleDao;
import ph.intrepidstream.callmanager.dao.impl.RuleDaoImpl;
import ph.intrepidstream.callmanager.db.DBHelper;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.ui.DialogActivity;
import ph.intrepidstream.callmanager.util.AppGlobal;
import ph.intrepidstream.callmanager.util.Country;
import ph.intrepidstream.callmanager.util.RuleState;

public class OutgoingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Country country = getSelectedCountry(context);
            phoneNumber = replaceCountryCode(phoneNumber, country);
            Rule rule = getApplicableRule(context, phoneNumber, country);
            if (rule == null) {
                return;
            }

            if (rule.getState() == RuleState.WARN) {
                if (!AppGlobal.shouldProceedCall) {
                    setResultData(null);
                    showWarningDialog(context, rule.getName(), phoneNumber);
                } else {
                    AppGlobal.shouldProceedCall = false;
                }
            } else if (rule.getState() == RuleState.BLOCK) {
                setResultData(null);
                showBlockedDialog(context, rule.getName());
            }
        }
    }

    private String replaceCountryCode(String phoneNumber, Country country) {
        if (phoneNumber.startsWith(country.getCountryCode())) {
            phoneNumber = phoneNumber.replaceFirst("\\" + country.getCountryCode(), "0");
        }
        return phoneNumber;
    }

    private Rule getApplicableRule(Context context, String phoneNumber, Country country) {
        Rule applicableRule = null;
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        RuleDao ruleDao = RuleDaoImpl.getInstance();
        List<Rule> rules = ruleDao.retrieveRulesByCountry(db, country);
        Iterator<Rule> ruleIterator = rules.iterator();
        Rule rule;
        while (applicableRule == null && ruleIterator.hasNext()) {
            rule = ruleIterator.next();
            if (rule.getState() != RuleState.OFF && rule.isIncluded(phoneNumber)) {
                applicableRule = rule;
            }
        }
        return applicableRule;
    }

    private void showWarningDialog(Context context, String operatorName, String phoneNumber) {
        Intent dialogIntent = new Intent(context, DialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(DialogActivity.EXTRA_OPERATOR_NAME, operatorName);
        dialogIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
        dialogIntent.putExtra(DialogActivity.EXTRA_DIALOG_TYPE, RuleState.WARN.toString());
        context.startActivity(dialogIntent);
    }

    private void showBlockedDialog(Context context, String operatorName) {
        Intent dialogIntent = new Intent(context, DialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(DialogActivity.EXTRA_OPERATOR_NAME, operatorName);
        dialogIntent.putExtra(DialogActivity.EXTRA_DIALOG_TYPE, RuleState.BLOCK.toString());
        context.startActivity(dialogIntent);
    }

    private Country getSelectedCountry(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String selected = sharedPref.getString(context.getString(R.string.call_manager_country), Country.NONE.name());
        return Country.valueOf(selected);
    }

}
