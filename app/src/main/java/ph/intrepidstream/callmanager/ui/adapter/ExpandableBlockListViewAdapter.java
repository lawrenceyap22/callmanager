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
        if (convertView == null) {
            layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.blocklist_child, null);
        }

        List<Condition> conditions = (List<Condition>) getChild(groupPosition, childPosition);

        FlowLayout numbersLayout = (FlowLayout) convertView.findViewById(R.id.blocklist_child_numbers);
        numbersLayout.removeAllViews();

        AppCompatImageButton editButton = (AppCompatImageButton) convertView.findViewById(R.id.blocklist_child_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRule((Rule) getGroup(groupPosition));
            }
        });

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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
