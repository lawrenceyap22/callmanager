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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import ph.intrepidstream.callmanager.util.Country;

public class AddRuleActivity extends AppCompatActivity {

    private final String TAG = AddRuleActivity.class.getName();

    private RuleDao ruleDao;
    private EditText nameEditText;
    private EditText numberEditText;
    private FlowLayout numbersLayout;
    private List<RuleItemView> ruleItems;
    private Rule rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);

        ruleDao = RuleDaoImpl.getInstance();
        initViews();
        initViewValuesIfEdit();

        //Set up action bar last for custom texts of Add and Edit Rules
        ActionBar actionBar = getSupportActionBar();
        initCustomActionBar(actionBar);
    }

    private void initCustomActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.actionbar_add_rule_custom, null);
        TextView title = (TextView) view.findViewById(R.id.add_rule_label);
        Button button = (Button) view.findViewById(R.id.add_rule_button);

        if (rule != null) {
            title.setText(getString(R.string.add_rule_edit_label));
            button.setText(getString(R.string.add_rule_edit_button));
        } else {
            title.setText(getString(R.string.add_rule_add_label));
            button.setText(getString(R.string.add_rule_add_button));
        }

        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void initViews() {
        nameEditText = (EditText) findViewById(R.id.add_rule_name);
        numberEditText = (EditText) findViewById(R.id.add_rule_text);
        AppCompatImageButton addButton = (AppCompatImageButton) findViewById(R.id.add_rule_add);
        addButton.setVisibility(View.INVISIBLE);
        numbersLayout = (FlowLayout) findViewById(R.id.add_rule_list);
        ruleItems = new ArrayList<>();
        setUpTextChangedListener(numberEditText, addButton);
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
                addRuleItem(condition.getNumber(), numbersLayout, ruleItems);
            }
        }
    }

    public void saveRule(View view) {
        if (isNameValid()) {
            if (rule != null) {
                updateOrDeleteRule();
            } else if (isValidToInsert()) {
                insertRule();
                done();
            }
        }
    }

    private boolean isNameValid() {
        boolean valid = true;
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            valid = false;
            nameEditText.setError(getString(R.string.add_rule_name_required));
        } else {
            DBHelper dbHelper = DBHelper.getInstance(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Rule dbRule = ruleDao.getRuleByName(db, name);

            if (dbRule != null && (rule == null || !dbRule.getId().equals(rule.getId()))) {
                valid = false;
                nameEditText.setError(getString(R.string.add_rule_name_exist));
            }
        }
        return valid;
    }

    private void done() {
        setResult(RESULT_OK);
        finish();
    }

    private void updateOrDeleteRule() {
        if (ruleItems.isEmpty()) {
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
        } else {
            updateRule();
            done();
        }
    }

    private boolean isValidToInsert() {
        boolean valid = true;
        if (ruleItems.isEmpty()) {
            numberEditText.setError(getString(R.string.add_rule_condition_required));
            valid = false;
        }
        return valid;
    }

    private void insertRule() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        rule = new Rule();
        rule.setName(nameEditText.getText().toString().trim());
        rule.setIsAppGenerated(false);
        rule.setConditions(getAllConditions(rule.getId()));
        rule.setCountry(Country.NONE);

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
        newRule.setName(nameEditText.getText().toString().trim());
        newRule.setState(rule.getState());
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
        List<Condition> conditions = new ArrayList<>(ruleItems.size());
        Condition condition;
        for (RuleItemView itemView : ruleItems) {
            condition = new Condition();
            condition.setRuleId(ruleId);
            condition.setNumber(itemView.getText());
            conditions.add(condition);
        }

        return conditions;
    }

    public void addInput(View view) {
        String number = numberEditText.getText().toString();
        if (addRuleItem(number, numbersLayout, ruleItems)) {
            numberEditText.setText("");
        } else {
            numberEditText.setError(getString(R.string.add_rule_duplicate_number));
        }
    }

    private boolean addRuleItem(String number, FlowLayout numbersLayout, List<RuleItemView> ruleItems) {
        boolean addSuccessful = false;
        RuleItemView ruleItem = new RuleItemView(this, number, ruleItems, numbersLayout);
        if (!ruleItems.contains(ruleItem)) {
            ruleItems.add(ruleItem);
            numbersLayout.addView(ruleItem.getView());
            addSuccessful = true;
        }
        return addSuccessful;
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
                || !numberEditText.getText().toString().isEmpty()
                || !ruleItems.isEmpty();
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