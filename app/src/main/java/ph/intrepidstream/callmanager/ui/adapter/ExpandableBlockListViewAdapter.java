package ph.intrepidstream.callmanager.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
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

        Rule rule = (Rule) getGroup(groupPosition);
        TextView textView = (TextView) convertView.findViewById(R.id.blocklist_group_name_textview);
        textView.setText(rule.getName());

        MultiStateToggleButton multiStateToggleButton = (MultiStateToggleButton) convertView.findViewById(R.id.blocklist_group_state_toggle);
        multiStateToggleButton.setCurrentState(rule.getState().ordinal()); // TODO: Replace ordinal() with something else

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
