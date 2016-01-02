package ph.intrepidstream.callmanager.ui;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ph.intrepidstream.callmanager.R;

public class RuleFieldView {

    private Context context;
    private Spinner conditions;
    private EditText number;
    private ImageButton cancel;
    private View view;
    private Map<String, Integer> conditionValues;
    private List<RuleFieldView> ruleFields;
    private LinearLayout parentLayout;

    public RuleFieldView(Context context, List<RuleFieldView> ruleFields, LinearLayout parentLayout) {
        this.context = context;
        this.ruleFields = ruleFields;
        this.parentLayout = parentLayout;
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.rule_field_layout, null);

        initConditionsSpinner();
        initNumberEditText();
        initCancelButton();
    }

    private void initConditionsSpinner() {
        conditions = (Spinner) view.findViewById(R.id.add_rule_condition);
        setSpinnerList(conditions);
    }

    private void initNumberEditText() {
        final Button addRuleAnotherFieldButton = (Button) ((Activity) context).getWindow().getDecorView().findViewById(R.id.add_rule_another_field);
        number = (EditText) view.findViewById(R.id.add_rule_number);
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    boolean hasBlankField = false;
                    for (RuleFieldView ruleFieldView : ruleFields) {
                        if (ruleFieldView.getNumberText().isEmpty()) {
                            hasBlankField = true;
                            break;
                        }
                    }
                    if (!hasBlankField) {
                        addRuleAnotherFieldButton.setVisibility(View.VISIBLE);
                    } else {
                        addRuleAnotherFieldButton.setVisibility(View.INVISIBLE);
                    }
                } else {
                    addRuleAnotherFieldButton.setVisibility(View.INVISIBLE);
                    for (RuleFieldView ruleFieldView : ruleFields) {
                        if (ruleFieldView.getNumberText().isEmpty() && ruleFieldView != RuleFieldView.this) {
                            removeFromLayout(ruleFieldView);
                            break;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initCancelButton() {
        cancel = (ImageButton) view.findViewById(R.id.add_rule_field_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromLayout(RuleFieldView.this);
            }
        });
    }

    private void removeFromLayout(RuleFieldView ruleFieldView) {
        if (ruleFields.size() > 1) {
            parentLayout.removeView(ruleFieldView.getView());
            ruleFields.remove(ruleFieldView);
        } else {
            number.setText("");
        }
    }

    private void setSpinnerList(Spinner spinner) {
        conditionValues = new LinkedHashMap<>(4);
        conditionValues.put("starts with", 0);
        conditionValues.put("equals", 1);
        conditionValues.put("not starts with", 2);
        conditionValues.put("not equals", 3);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, conditionValues.keySet().toArray(new String[4]));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public Integer getSelectedConditionValue() {
        return conditionValues.get(getSelectedCondition());
    }

    public String getSelectedCondition() {
        return conditions.getSelectedItem().toString();
    }

    public String getNumberText() {
        return number.getText().toString();
    }

    public View getView() {
        return view;
    }
}
