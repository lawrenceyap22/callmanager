package ph.intrepidstream.callmanager.ui;

import android.app.AlertDialog;
import android.content.ContentValues;
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
import java.util.Iterator;
import java.util.List;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.db.DBHelper;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.util.ConditionLookup;
import ph.intrepidstream.callmanager.util.RuleState;

import static ph.intrepidstream.callmanager.db.CallManagerDatabaseContract.ConditionEntry;
import static ph.intrepidstream.callmanager.db.CallManagerDatabaseContract.RuleEntry;

public class AddRuleActivity extends AppCompatActivity {

    private final String TAG = AddRuleActivity.class.getName();

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
                    addRuleItem(condition.getNumber(), startsWithLayout, startsWithRuleItems);
                } else {
                    equalsSpinner.setSelection(condition.getLookup() == ConditionLookup.EQUALS ? 0 : 1);
                    addRuleItem(condition.getNumber(), equalsLayout, equalsRuleItems);
                }
            }
        }
    }

    private void addRuleItem(String input, FlowLayout parentLayout, List<RuleItemView> itemList) {
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
        Long newId;

        ContentValues contentValues = new ContentValues();
        contentValues.put(RuleEntry.COLUMN_NAME_NAME, nameEditText.getText().toString());
        contentValues.put(RuleEntry.COLUMN_NAME_STATE, RuleState.OFF.name());
        contentValues.put(RuleEntry.COLUMN_NAME_APP_GENERATED, true);
        db.beginTransaction();
        newId = db.insert(RuleEntry.TABLE_NAME, null, contentValues);
        if (newId != -1) {
            boolean hasError = false;
            Iterator<RuleItemView> itemViewIterator = startsWithRuleItems.iterator();
            while (!hasError && itemViewIterator.hasNext()) {
                contentValues = new ContentValues();
                contentValues.put(ConditionEntry.COLUMN_NAME_RULE_ID, newId);
                contentValues.put(ConditionEntry.COLUMN_NAME_LOOKUP, getConditionLookup(startsWithSpinner).name());
                contentValues.put(ConditionEntry.COLUMN_NAME_NUMBER, itemViewIterator.next().getText());
                if (db.insert(ConditionEntry.TABLE_NAME, null, contentValues) == -1) {
                    hasError = true;
                }
            }

            if (!hasError) {
                itemViewIterator = equalsRuleItems.iterator();
                while (!hasError && itemViewIterator.hasNext()) {
                    contentValues = new ContentValues();
                    contentValues.put(ConditionEntry.COLUMN_NAME_RULE_ID, newId);
                    contentValues.put(ConditionEntry.COLUMN_NAME_LOOKUP, getConditionLookup(equalsSpinner).name());
                    contentValues.put(ConditionEntry.COLUMN_NAME_NUMBER, itemViewIterator.next().getText());
                    if (db.insert(ConditionEntry.TABLE_NAME, null, contentValues) == -1) {
                        hasError = true;
                    }
                }

                if (!hasError) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, nameEditText.getText().toString() + " inserted successfully.");
                    }
                    db.setTransactionSuccessful();
                }
            }
        }
        db.endTransaction();
    }

    private void updateRule() {
        boolean hasError = false;
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        if (!nameEditText.getText().toString().equals(rule.getName())) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RuleEntry.COLUMN_NAME_NAME, nameEditText.getText().toString());
            String whereClause = RuleEntry._ID + "=?";
            String[] whereClauseArgs = new String[]{rule.getId().toString()};

            if (db.update(RuleEntry.TABLE_NAME, contentValues, whereClause, whereClauseArgs) == 0) {
                hasError = true;
            }
        }

        if (!hasError) {
            List<String> startsWithNumbers = new ArrayList<>(startsWithRuleItems.size());
            for (RuleItemView itemView : startsWithRuleItems) {
                startsWithNumbers.add(itemView.getText());
            }

            List<String> equalsNumbers = new ArrayList<>(equalsRuleItems.size());
            for (RuleItemView itemView : equalsRuleItems) {
                equalsNumbers.add(itemView.getText());
            }

            Condition oldCondition;
            Iterator<Condition> conditionIterator = rule.getConditions().iterator();
            while (!hasError && conditionIterator.hasNext()) {
                oldCondition = conditionIterator.next();
                if (oldCondition.getLookup() == ConditionLookup.STARTS_WITH || oldCondition.getLookup() == ConditionLookup.NOT_STARTS_WITH) {
                    hasError = updateDeleteCondition(db, oldCondition, startsWithNumbers, equalsNumbers, startsWithSpinner, equalsSpinner);
                } else {
                    hasError = updateDeleteCondition(db, oldCondition, equalsNumbers, startsWithNumbers, equalsSpinner, startsWithSpinner);
                }
            }
            if (!hasError) {
                db.setTransactionSuccessful();
            }
        }
        db.endTransaction();
    }

    private void deleteRuleAndConditions() {
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String ruleWhereClause = RuleEntry._ID + "=?";
        String conditionWhereClause = ConditionEntry.COLUMN_NAME_RULE_ID + "=?";
        String[] whereClauseArgs = {rule.getId().toString()};

        db.beginTransaction();
        if (db.delete(ConditionEntry.TABLE_NAME, conditionWhereClause, whereClauseArgs) > 0) {
            if (db.delete(RuleEntry.TABLE_NAME, ruleWhereClause, whereClauseArgs) > 0) {
                db.setTransactionSuccessful();
            }
        }
        db.endTransaction();
    }

    private boolean updateDeleteCondition(SQLiteDatabase db, Condition oldCondition, List<String> mainListNumbers, List<String> secondaryListNumbers, Spinner mainSpinner, Spinner secondarySpinner) {
        String whereClause = ConditionEntry._ID + "=?";
        String[] whereClauseArgs = {oldCondition.getId().toString()};
        boolean hasError = false;
        if (!mainListNumbers.contains(oldCondition.getNumber())) {
            if (secondaryListNumbers.contains(oldCondition.getNumber())) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ConditionEntry.COLUMN_NAME_LOOKUP, getConditionLookup(secondarySpinner).name());
                if (db.update(ConditionEntry.TABLE_NAME, contentValues, whereClause, whereClauseArgs) == 0) {
                    hasError = true;
                }
            } else {
                if (db.delete(ConditionEntry.TABLE_NAME, whereClause, whereClauseArgs) == 0) {
                    hasError = true;
                }
            }
        } else {
            if (oldCondition.getLookup() != getConditionLookup(mainSpinner)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ConditionEntry.COLUMN_NAME_LOOKUP, getConditionLookup(mainSpinner).name());
                if (db.update(ConditionEntry.TABLE_NAME, contentValues, whereClause, whereClauseArgs) == 0) {
                    hasError = true;
                }
            }
        }
        return hasError;
    }

    private ConditionLookup getConditionLookup(Spinner spinner) {
        return ConditionLookup.findByDisplayText(spinner.getSelectedItem().toString());
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
