package ph.intrepidstream.callmanager.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.ui.AddRuleActivity;
import ph.intrepidstream.callmanager.ui.MainActivity;
import ph.intrepidstream.callmanager.ui.custom.MultiStateToggleButton;
import ph.intrepidstream.callmanager.util.ConditionLookup;

public class ExpandableBlockListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Rule> rules;

    public ExpandableBlockListViewAdapter(Context context, List<Rule> rules) {
        this.context = context;
        this.rules = rules;
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
//        return rules.get(groupPosition).getConditions().size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return rules.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
//        return rules.get(groupPosition).getConditions().get(childPosition);
        return rules.get(groupPosition).getConditions();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return rules.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
//        return rules.get(groupPosition).getConditions().get(childPosition).getId();
        return rules.get(groupPosition).getId();
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

        Rule rule = (Rule) getGroup(groupPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.blocklist_group_name_textview);
        textView.setText(rule.getName());

        MultiStateToggleButton multiStateToggleButton = (MultiStateToggleButton) convertView.findViewById(R.id.blocklist_group_state_toggle);
        multiStateToggleButton.setCurrentState(rule.getState().ordinal()); // TODO: Replace ordinal() with something else

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = null;
        boolean hasStartsWith = false;
        boolean hasEquals = false;
        if (convertView == null) {
            layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.blocklist_child, null);
        }

        List<Condition> conditions = (List<Condition>) getChild(groupPosition, childPosition);
        TextView number;
        TextView startsWithLookup = (TextView) convertView.findViewById(R.id.blocklist_child_starts_with_lookup);
        startsWithLookup.setText("");
        startsWithLookup.setVisibility(View.GONE);

        TextView equalsLookup = (TextView) convertView.findViewById(R.id.blocklist_child_equals_lookup);
        equalsLookup.setText("");
        equalsLookup.setVisibility(View.GONE);

        FlowLayout startsWithLayout = (FlowLayout) convertView.findViewById(R.id.blocklist_child_starts_with_conditions);
        startsWithLayout.removeAllViews();
        startsWithLayout.setVisibility(View.GONE);

        FlowLayout equalsLayout = (FlowLayout) convertView.findViewById(R.id.blocklist_child_equals_conditions);
        equalsLayout.removeAllViews();
        equalsLayout.setVisibility(View.GONE);

        AppCompatImageButton startsWithEditRuleBtn = (AppCompatImageButton) convertView.findViewById(R.id.blocklist_child_starts_with_edit);
        startsWithEditRuleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRule((Rule) getGroup(groupPosition));
            }
        });

        View childView;
        for (Condition condition : conditions) {
            if (layoutInflater == null) {
                layoutInflater = LayoutInflater.from(context);
            }
            childView = layoutInflater.inflate(R.layout.item_blocklist_child, null);
            number = (TextView) childView.findViewById(R.id.blocklist_child_number);
            number.setText(condition.getNumber());

            if (condition.getLookup() == ConditionLookup.STARTS_WITH || condition.getLookup() == ConditionLookup.NOT_STARTS_WITH) {
                if (!startsWithLookup.getText().toString().equals(condition.getLookup().toString())) {
                    startsWithLookup.setText(condition.getLookup().toString());
                }
                startsWithLayout.addView(childView);
                hasStartsWith = true;
            } else {
                if (!equalsLookup.getText().toString().equals(condition.getLookup().toString())) {
                    equalsLookup.setText(condition.getLookup().toString());
                }
                equalsLayout.addView(childView);
                hasEquals = true;
            }
        }

        if (hasStartsWith) {
            startsWithLookup.setVisibility(View.VISIBLE);
            startsWithLayout.setVisibility(View.VISIBLE);
        }

        if (hasEquals) {
            equalsLookup.setVisibility(View.VISIBLE);
            equalsLayout.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private void editRule(Rule rule) {
        Intent editRuleIntent = new Intent(context, AddRuleActivity.class);
        editRuleIntent.putExtra(MainActivity.EXTRA_EDIT_RULE, rule);
        ((Activity) context).startActivityForResult(editRuleIntent, MainActivity.ADD_EDIT_RULE_REQUEST);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
