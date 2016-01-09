package ph.intrepidstream.callmanager.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.db.DBHelper;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.util.ConditionLookup;
import ph.intrepidstream.callmanager.util.RuleState;

import static ph.intrepidstream.callmanager.db.CallManagerDatabaseContract.ConditionEntry;
import static ph.intrepidstream.callmanager.db.CallManagerDatabaseContract.RuleEntry;

public class ExpandableBlockListViewAdapter extends BaseExpandableListAdapter {
    private final String TAG = ExpandableBlockListViewAdapter.class.getName();

    private Context context;
    private List<Rule> rules;

    public ExpandableBlockListViewAdapter(Context context) {
        this.context = context;
        retrieveRules();
    }

    private void retrieveRules() {
        rules = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {RuleEntry._ID, RuleEntry.COLUMN_NAME_NAME, RuleEntry.COLUMN_NAME_STATE, RuleEntry.COLUMN_NAME_APP_GENERATED};
        String sortOrder = RuleEntry._ID;
        Cursor cursor = db.query(RuleEntry.TABLE_NAME, columns, null, null, null, null, sortOrder);

        if (cursor.moveToFirst()) {
            do {
                Rule rule = new Rule();
                rule.setId(cursor.getLong(cursor.getColumnIndex(RuleEntry._ID)));
                rule.setName(cursor.getString(cursor.getColumnIndex(RuleEntry.COLUMN_NAME_NAME)));
                rule.setState(RuleState.valueOf(cursor.getString(cursor.getColumnIndex(RuleEntry.COLUMN_NAME_STATE))));
                rule.setIsAppGenerated(cursor.getInt(cursor.getColumnIndex(RuleEntry.COLUMN_NAME_APP_GENERATED)) != 0);
                rule.setConditions(retrieveConditions(rule.getId(), db));
                rules.add(rule);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Retrieved Rule: " + rule.toString());
                }
            } while (cursor.moveToNext());
        }

    }

    private List<Condition> retrieveConditions(Long ruleId, SQLiteDatabase db) {
        List<Condition> conditions = new ArrayList<>();
        String[] columns = {ConditionEntry._ID, ConditionEntry.COLUMN_NAME_RULE_ID, ConditionEntry.COLUMN_NAME_LOOKUP, ConditionEntry.COLUMN_NAME_NUMBER};
        String whereClause = ConditionEntry.COLUMN_NAME_RULE_ID + "=?";
        String[] whereClauseArgs = {ruleId.toString()};
        String sortOrder = ConditionEntry._ID;
        Cursor cursor = db.query(ConditionEntry.TABLE_NAME, columns, whereClause, whereClauseArgs, null, null, sortOrder);

        if (cursor.moveToFirst()) {
            do {
                Condition condition = new Condition();
                condition.setId(cursor.getLong(cursor.getColumnIndex(ConditionEntry._ID)));
                condition.setRuleId(cursor.getLong(cursor.getColumnIndex(ConditionEntry.COLUMN_NAME_RULE_ID)));
                condition.setLookup(ConditionLookup.valueOf(cursor.getString(cursor.getColumnIndex(ConditionEntry.COLUMN_NAME_LOOKUP))));
                condition.setNumber(cursor.getString(cursor.getColumnIndex(ConditionEntry.COLUMN_NAME_NUMBER)));
                conditions.add(condition);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Retrieved Condition: " + condition.toString());
                }
            } while (cursor.moveToNext());
        }
        return conditions;
    }

    @Override
    public int getGroupCount() {
        return rules.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return rules.get(groupPosition).getConditions().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return rules.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return rules.get(groupPosition).getConditions().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return rules.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return rules.get(groupPosition).getConditions().get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // TODO: Modify for nicer view
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.blocklist_group, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.blocklist_group_name_textview);
        textView.setText(((Rule) getGroup(groupPosition)).getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO: Modify to properly show condition list
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.blocklist_child, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.blocklist_child_name_textview);
        textView.setText(((Condition) getChild(groupPosition, childPosition)).getNumber());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
