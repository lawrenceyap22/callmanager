package ph.intrepidstream.callmanager.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.util.Condition;
import ph.intrepidstream.callmanager.util.DBHelper;
import ph.intrepidstream.callmanager.util.Rule;

/**
 * Created by Jayzon on 2015/12/30.
 */
public class ExpandableBlockListViewAdapter extends BaseExpandableListAdapter {
    private Context context;

    private List<Rule> ruleList;
    private List<Condition> conditionList;

    public ExpandableBlockListViewAdapter(Context context, DBHelper dbHelper, SQLiteDatabase db) {
        this.context = context;

        ruleList = new ArrayList<Rule>();
        conditionList = new ArrayList<Condition>();

        Cursor ruleCursor = dbHelper.getRules(db);
        if (ruleCursor.moveToFirst()) {
            do {
                Rule rule = new Rule();
                int ruleID = ruleCursor.getInt(ruleCursor.getColumnIndex(dbHelper.RULE_COLUMN_ID));
                rule.setName(ruleCursor.getString(ruleCursor.getColumnIndex(dbHelper.RULE_COLUMN_NAME)));
                rule.setState(ruleCursor.getInt(ruleCursor.getColumnIndex(dbHelper.RULE_COLUMN_STATE)));
                ruleList.add(rule);

                Cursor conditionCursor = dbHelper.getConditions(db,ruleID);
                if(conditionCursor.moveToFirst()){
                    do{
                        Condition condition = new Condition();
                        condition.setRule_id(conditionCursor.getInt(conditionCursor.getColumnIndex(dbHelper.CONDITION_COLUMN_RULE_ID)));
                        condition.setCode(conditionCursor.getInt(conditionCursor.getColumnIndex(dbHelper.CONDITION_COLUMN_CODE)));
                        condition.setNumber(conditionCursor.getString(conditionCursor.getColumnIndex(dbHelper.CONDITION_COLUMN_NUMBER)));
                        conditionList.add(condition);
                    } while(conditionCursor.moveToNext());
                }


            } while (ruleCursor.moveToNext());
        }
    }

    private List<?> retrieveListItems() {
        // TODO: Retrieve from database
        return null;
    }

    @Override
    public int getGroupCount() {
        // TODO: Modify
        return ruleList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO: Modify
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO: Modify
        return ruleList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO: Modify
        return conditionList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO: Modify
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO: Modify
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // TODO: Modify
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.blocklist_group, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.blocklist_group_name_textview);
        textView.setText(ruleList.get(groupPosition).toString());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO: Modify
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.blocklist_child, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.blocklist_child_name_textview);
        textView.setText(conditionList.get(groupPosition).toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
