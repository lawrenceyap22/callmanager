package ph.intrepidstream.callmanager.ui.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.R;

/**
 * Created by Jayzon on 2015/12/30.
 */
public class ExpandableBlockListViewAdapter extends BaseExpandableListAdapter {
    private Context context;

    private List<String> listItems;

    public ExpandableBlockListViewAdapter(Context context) {
        this.context = context;

        // Test code
        listItems = new ArrayList<String>();
        for(int i = 1; i <= 10; i++) {
            listItems.add("Test item # " + i);
        }
    }

    private List<?> retrieveListItems() {
        // TODO: Retrieve from database
        return null;
    }

    @Override
    public int getGroupCount() {
        // TODO: Modify
        return listItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO: Modify
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO: Modify
        return listItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO: Modify
        return listItems.get(groupPosition);
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
        textView.setText(listItems.get(groupPosition));

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
        textView.setText(listItems.get(groupPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
