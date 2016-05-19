package ph.intrepidstream.callmanager.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public final class ListViewUtil {

    private ListViewUtil() {
    }

    public static void setListViewHeightBasedOnChildren(ExpandableListView expandableListView) {
        ExpandableListAdapter listAdapter = expandableListView.getExpandableListAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = expandableListView.getPaddingTop() + expandableListView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupView = listAdapter.getGroupView(i, false, null, expandableListView);
            if (groupView instanceof ViewGroup) {
                groupView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            groupView.measure(0, height);
            totalHeight += groupView.getMeasuredHeight();

            if (expandableListView.isGroupExpanded(i)) {
                int childWidth;
                int childHeight;
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View childView = listAdapter.getChildView(i, j, false, null, expandableListView);
                    if (childView instanceof ViewGroup) {
                        groupView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    childWidth = View.MeasureSpec.makeMeasureSpec(expandableListView.getWidth(), View.MeasureSpec.AT_MOST);
                    childHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    childView.measure(childWidth, childHeight);
                    totalHeight += childView.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        params.height = totalHeight + (expandableListView.getDividerHeight() * (listAdapter.getGroupCount() - 1));

        expandableListView.setLayoutParams(params);
        expandableListView.requestLayout();
    }
}
