package ph.intrepidstream.callmanager.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.dao.RuleDao;
import ph.intrepidstream.callmanager.dao.impl.RuleDaoImpl;
import ph.intrepidstream.callmanager.db.DBHelper;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.util.ConditionLookup;

public class AddRuleActivity extends AppCompatActivity {

    private final String TAG = AddRuleActivity.class.getName();

    private RuleDao ruleDao;
    private EditText nameEditText;
    private Spinner startsWithSpinner;
    private EditText startsWithText;
    private FlowLayout startsWithLayout;
    private List<RuleItemView> startsWithRuleItems;
    private Spinner equalsSpinner;
    private EditText equalsText;
    private FlowLayout equalsLayout;
    private List<RuleItemView> equalsRuleItems;
    private Rule rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);

        ruleDao = RuleDaoImpl.getInstance();

        ActionBar actionBar = getSupportActionBar();
        initCustomActionBar(actionBar);

        nameEditText = (EditText) findViewById(R.id.add_rule_name);
        initStartsWithViews();
        initEqualsViews();

        initViewValuesIfEdit();
    }

    private void initCustomActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.actionbar_add_rule_custom, null);

        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void initStartsWithViews() {
        startsWithSpinner = (Spinner) findViewById(R.id.add_rule_starts_with_spinner);
        startsWithText = (EditText) findViewById(R.id.add_rule_starts_with_text);
        AppCompatImageButton startsWithAdd = (AppCompatImageButton) findViewById(R.id.add_rule_starts_with_add);
        startsWithAdd.setVisibility(View.INVISIBLE);
        startsWithLayout = (FlowLayout) findViewById(R.id.add_rule_starts_with_list);
        startsWithRuleItems = new ArrayList<>();
        setUpSpinner(startsWithSpinner, new String[]{ConditionLookup.STARTS_WITH.toString(), ConditionLookup.NOT_STARTS_WITH.toString()});
        setUpTextChangedListener(startsWithText, startsWithAdd);
    }

    private void initEqualsViews() {
        equalsSpinner = (Spinner) findViewById(R.id.add_rule_equals_spinner);
        equalsText = (EditText) findViewById(R.id.add_rule_equals_text);
        AppCompatImageButton equalsAdd = (AppCompatImageButton) findViewById(R.id.add_rule_equals_add);
        equalsAdd.setVisibility(View.INVISIBLE);
        equalsLayout = (FlowLayout) findViewById(R.id.add_rule_equals_list);
        equalsRuleItems = new ArrayList<>();
        setUpSpinner(equalsSpinner, new String[]{ConditionLookup.EQUALS.toString(), ConditionLookup.NOT_EQUALS.toString()});
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

    private void initViewValuesIfEdit() {
        rule = getIntent().getParcelableExtra(MainActivity.EXTRA_EDIT_RULE);
        if (rule != null) {
            nameEditText.setText(rule.getName());
            for (Condition condition : rule.getConditions()) {
                if (condition.getLookup() == ConditionLookup.STARTS_WITH || condition.getLookup() == ConditionLookup.NOT_STARTS_WITH) {
                    startsWithSpinner.setSelection(condition.getLookup() == ConditionLookup.STARTS_WITH ? 0 : 1);
                    addRuleItemEdit(condition.getNumber(), startsWithLayout, startsWithRuleItems);
                } else {
                    equalsSpinner.setSelection(condition.getLookup() == ConditionLookup.EQUALS ? 0 : 1);
                    addRuleItemEdit(condition.getNumber(), equalsLayout, equalsRuleItems);
                }
            }
        }
    }

    private void addRuleItemEdit(String input, FlowLayout parentLayout, List<RuleItemView> itemList) {
        RuleItemView ruleItem = new RuleItemView(this, input, itemList, parentLayout);
        if (!itemList.contains(ruleItem)) {
            itemList.add(ruleItem);
            parentLayout.addView(ruleItem.getView());
        }
    }

    public void saveRule(View view) {
        if (rule != null) {
            updateOrDeleteRule();
        } else {
            if (isValidToInsert()) {
                insertRule();
                done();
            }
        }
    }

    private void done() {
        setResult(RESULT_OK);
        finish();
    }

    private void updateOrDeleteRule() {
        if (startsWithRuleItems.isEmpty() && equalsRuleItems.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_rule_warning_no_conditions)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRuleAndConditions();
                            dialog.cancel();
                            done();
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
        } else if (!nameEditText.getText().toString().isEmpty()) {
            updateRule();
            done();
        } else {
            nameEditText.setError(getString(R.string.add_rule_name_required));
        }
    }

    private boolean isValidToInsert() {
        boolean valid = true;
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError(getString(R.string.add_rule_name_required));
            valid = false;
        }

        if (startsWithRuleItems.isEmpty() && equalsRuleItems.isEmpty()) {
            startsWithText.setError(getString(R.string.add_rule_condition_required));
            equalsText.setError(getString(R.string.add_rule_condition_required));
            valid = false;
        }
        return valid;
    }

    private void insertRule() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        rule = new Rule();
        rule.setName(nameEditText.getText().toString());
        rule.setIsAppGenerated(false);
        rule.setConditions(getAllConditions(rule.getId()));

        db.beginTransaction();
        if (ruleDao.insertRule(db, rule) != -1) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, rule.getName() + " inserted successfully.");
            }
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }

    private void updateRule() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Rule newRule = new Rule();
        newRule.setName(nameEditText.getText().toString());
        newRule.setConditions(getAllConditions(rule.getId()));

        db.beginTransaction();
        if (ruleDao.updateRule(db, rule, newRule)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Rule updated successfully.");
            }
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }

    private void deleteRuleAndConditions() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        if (ruleDao.deleteRule(db, rule.getId()) > 0) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Rule deleted successfully.");
            }
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }

    private List<Condition> getAllConditions(Long ruleId) {
        List<Condition> conditions = new ArrayList<>(startsWithRuleItems.size() + equalsRuleItems.size());
        Condition condition;
        for (RuleItemView itemView : startsWithRuleItems) {
            condition = new Condition();
            if (ruleId != null) {
                condition.setRuleId(ruleId);
            }
            condition.setLookup(getConditionLookup(startsWithSpinner));
            condition.setNumber(itemView.getText());
            conditions.add(condition);
        }

        for (RuleItemView itemView : equalsRuleItems) {
            condition = new Condition();
            if (ruleId != null) {
                condition.setRuleId(ruleId);
            }
            condition.setLookup(getConditionLookup(equalsSpinner));
            condition.setNumber(itemView.getText());
            conditions.add(condition);
        }
        return conditions;
    }

    private ConditionLookup getConditionLookup(Spinner spinner) {
        return ConditionLookup.findByDisplayText(spinner.getSelectedItem().toString());
    }

    public void addStartsWithInput(View view) {
        addRuleItem(startsWithText, startsWithLayout, startsWithRuleItems, equalsRuleItems);
    }

    public void addEqualsInput(View view) {
        addRuleItem(equalsText, equalsLayout, equalsRuleItems, startsWithRuleItems);
    }

    private void addRuleItem(EditText editText, FlowLayout parentLayout, List<RuleItemView> itemList, List<RuleItemView> secondaryItemList) {
        String input = editText.getText().toString();
        RuleItemView ruleItem = new RuleItemView(this, input, itemList, parentLayout);
        if (!itemList.contains(ruleItem) && !secondaryItemList.contains(ruleItem)) {
            itemList.add(ruleItem);
            parentLayout.addView(ruleItem.getView());
            editText.setText("");
        } else {
            editText.setError(getString(R.string.add_rule_duplicate_number));
        }
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
                            setResult(RESULT_CANCELED);
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
