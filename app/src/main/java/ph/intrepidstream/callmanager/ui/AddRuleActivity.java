package ph.intrepidstream.callmanager.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.util.ConditionLookup;

public class AddRuleActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Spinner startsWithSpinner;
    private EditText startsWithText;
    private AppCompatImageButton startsWithAdd;
    private FlowLayout startsWithLayout;
    private List<RuleItemView> startsWithRuleItems;
    private Spinner equalsSpinner;
    private EditText equalsText;
    private AppCompatImageButton equalsAdd;
    private FlowLayout equalsLayout;
    private List<RuleItemView> equalsRuleItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);

        ActionBar actionBar = getSupportActionBar();
        initCustomActionBar(actionBar);

        nameEditText = (EditText) findViewById(R.id.add_rule_name);
        initStartsWithViews();
        initEqualsViews();
    }

    private void initCustomActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.custom_actionbar_add_rule, null);

        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void initStartsWithViews() {
        startsWithSpinner = (Spinner) findViewById(R.id.add_rule_starts_with_spinner);
        startsWithText = (EditText) findViewById(R.id.add_rule_starts_with_text);
        startsWithAdd = (AppCompatImageButton) findViewById(R.id.add_rule_starts_with_add);
        startsWithAdd.setVisibility(View.INVISIBLE);
        startsWithLayout = (FlowLayout) findViewById(R.id.add_rule_starts_with_list);
        startsWithRuleItems = new ArrayList<>();
        setUpSpinner(startsWithSpinner, new String[]{ConditionLookup.STARTS_WITH.getDisplayString(), ConditionLookup.NOT_STARTS_WITH.getDisplayString()});
        setUpTextChangedListener(startsWithText, startsWithAdd);
    }

    private void initEqualsViews() {
        equalsSpinner = (Spinner) findViewById(R.id.add_rule_equals_spinner);
        equalsText = (EditText) findViewById(R.id.add_rule_equals_text);
        equalsAdd = (AppCompatImageButton) findViewById(R.id.add_rule_equals_add);
        equalsAdd.setVisibility(View.INVISIBLE);
        equalsLayout = (FlowLayout) findViewById(R.id.add_rule_equals_list);
        equalsRuleItems = new ArrayList<>();
        setUpSpinner(equalsSpinner, new String[]{ConditionLookup.EQUALS.getDisplayString(), ConditionLookup.NOT_EQUALS.getDisplayString()});
        setUpTextChangedListener(equalsText, equalsAdd);
    }

    private void setUpSpinner(Spinner spinner, String[] values) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private void setUpTextChangedListener(EditText editText, final AppCompatImageButton imageButton) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imageButton.setVisibility(View.INVISIBLE);
                } else {
                    imageButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addRuleItem(String input, FlowLayout parentLayout, List<RuleItemView> itemList) {
        RuleItemView ruleItem = new RuleItemView(this, input, itemList, parentLayout);
        if (!itemList.contains(ruleItem)) {
            itemList.add(ruleItem);
            parentLayout.addView(ruleItem.getView());
        }
    }

    public void saveRule(View view) {
        //TODO: insert rule to database
    }

    public void addStartsWithInput(View view) {
        String input = startsWithText.getText().toString();
        addRuleItem(input, startsWithLayout, startsWithRuleItems);
        startsWithText.setText("");
    }

    public void addEqualsInput(View view) {
        String input = equalsText.getText().toString();
        addRuleItem(input, equalsLayout, equalsRuleItems);
        equalsText.setText("");
    }

    public void close(View view) {
        showDiscardChangesDialog();
    }

    @Override
    public void onBackPressed() {
        showDiscardChangesDialog();
    }

    private boolean hasChanges() {
        return !nameEditText.getText().toString().isEmpty()
                || !startsWithText.getText().toString().isEmpty()
                || !startsWithRuleItems.isEmpty()
                || !equalsText.getText().toString().isEmpty()
                || !equalsRuleItems.isEmpty();
    }

    private void showDiscardChangesDialog() {
        if (hasChanges()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.add_rule_discard_changes)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            finish();
        }
    }

}
