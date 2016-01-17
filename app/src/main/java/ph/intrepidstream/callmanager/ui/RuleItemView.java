package ph.intrepidstream.callmanager.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import ph.intrepidstream.callmanager.R;

public class RuleItemView {

    private Context context;
    private String text;
    private View view;
    private List<RuleItemView> ruleItems;
    private FlowLayout parentLayout;

    public RuleItemView(Context context, String text, List<RuleItemView> ruleItems, FlowLayout parentLayout) {
        this.context = context;
        this.text = text;
        this.ruleItems = ruleItems;
        this.parentLayout = parentLayout;
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item_add_rule, null);

        TextView textView = (TextView) view.findViewById(R.id.rule_item_text);
        textView.setText(text);

        ImageButton delete = (ImageButton) view.findViewById(R.id.rule_item_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });
    }

    private void deleteItem() {
        parentLayout.removeView(this.view);
        ruleItems.remove(this);
    }

    public String getText() {
        return text;
    }

    public View getView() {
        return view;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RuleItemView that = (RuleItemView) o;

        return new EqualsBuilder()
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(text)
                .toHashCode();
    }
}
