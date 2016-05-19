package ph.intrepidstream.callmanager.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.dao.RuleDao;
import ph.intrepidstream.callmanager.dao.impl.RuleDaoImpl;
import ph.intrepidstream.callmanager.db.DBHelper;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.ui.AddRuleActivity;
import ph.intrepidstream.callmanager.ui.MainActivity;
import ph.intrepidstream.callmanager.ui.custom.MultiStateSlider;
import ph.intrepidstream.callmanager.util.ListViewUtil;
import ph.intrepidstream.callmanager.util.RuleState;

public class ExpandableBlockListViewAdapter extends BaseExpandableListAdapter {

    private final String TAG = ExpandableBlockListViewAdapter.class.getName();

    private Context context;
    private List<Rule> rules;
    private ExpandableListView parent;

    public ExpandableBlockListViewAdapter(Context context, List<Rule> rules, ExpandableListView parent) {
        this.context = context;
        this.rules = rules;
        this.parent = parent;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public int getGroupCount() {
        return rules.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return rules.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return rules.get(groupPosition).getConditions();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return rules.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return rules.get(groupPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.blocklist_group, parent, false);
        }

        final Rule rule = (Rule) getGroup(groupPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.blocklist_group_name_textview);
        textView.setText(rule.getName());

        AppCompatImageButton editButton = (AppCompatImageButton) convertView.findViewById(R.id.blocklist_group_edit);
        AppCompatImageButton deleteButton = (AppCompatImageButton) convertView.findViewById(R.id.blocklist_group_delete);
        if (rule.isAppGenerated()) {
            editButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            editButton.setVisibility(View.VISIBLE);
            editButton.setFocusable(false);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setFocusable(false);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editRule(rule);
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    warnDeleteRule(rule);
                }
            });
        }

        MultiStateSlider multiStateSlider = (MultiStateSlider) convertView.findViewById(R.id.blocklist_group_state_toggle);
        multiStateSlider.clearListeners();
        multiStateSlider.setCurrentState(rule.getState().toString());
        multiStateSlider.addOnStateChangedListener(new MultiStateSlider.OnStateChangedListener() {
            @Override
            public void onStateChanged(String oldState, String newState) {
                if (!oldState.equals(newState)) {
                    Rule newRule = new Rule(rule);
                    newRule.setState(RuleState.findByDisplayText(newState));
                    updateRule(rule, newRule);
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = null;
        if (convertView == null) {
            layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.blocklist_child, parent, false);
        }

        @SuppressWarnings("unchecked")
        List<Condition> conditions = (List<Condition>) getChild(groupPosition, childPosition);

        FlowLayout numbersLayout = (FlowLayout) convertView.findViewById(R.id.blocklist_child_numbers);
        numbersLayout.removeAllViews();

        View childView;
        TextView number;
        for (Condition condition : conditions) {
            if (layoutInflater == null) {
                layoutInflater = LayoutInflater.from(context);
            }
            childView = layoutInflater.inflate(R.layout.item_blocklist_child, null);
            number = (TextView) childView.findViewById(R.id.blocklist_child_number);
            number.setText(condition.getNumber());
            numbersLayout.addView(childView);
        }
        return convertView;
    }

    private void editRule(Rule rule) {
        Intent editRuleIntent = new Intent(context, AddRuleActivity.class);
        editRuleIntent.putExtra(MainActivity.EXTRA_EDIT_RULE, rule);
        ((Activity) context).startActivityForResult(editRuleIntent, MainActivity.ADD_EDIT_RULE_REQUEST);
    }

    private void warnDeleteRule(final Rule rule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.delete_rule_warning)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRuleAndConditions(rule);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateRule(Rule oldRule, Rule newRule) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        RuleDao ruleDao = RuleDaoImpl.getInstance();
        db.beginTransaction();
        if (ruleDao.updateRule(db, oldRule, newRule)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Rule " + newRule.getName() + " updated to " + newRule.getState().toString());
            }
            oldRule.setState(newRule.getState());
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }

    private void deleteRuleAndConditions(Rule rule) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        RuleDao ruleDao = RuleDaoImpl.getInstance();
        db.beginTransaction();
        if (ruleDao.deleteRule(db, rule.getId()) > 0) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Rule deleted successfully.");
            }
            rules.remove(rule);
            notifyDataSetChanged();
            ListViewUtil.setListViewHeightBasedOnChildren(parent);
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
