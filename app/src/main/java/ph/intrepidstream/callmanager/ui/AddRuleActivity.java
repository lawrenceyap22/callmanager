package ph.intrepidstream.callmanager.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.R;

public class AddRuleActivity extends AppCompatActivity {

    private EditText nameEditText;
    private LinearLayout addRuleFieldsLayout;
    private Button addRuleAnotherFieldButton;
    private List<RuleFieldView> ruleFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);

        ActionBar actionBar = getSupportActionBar();
        setupCustomActionBar(actionBar);

        nameEditText = (EditText) findViewById(R.id.add_rule_name);
        addRuleAnotherFieldButton = (Button) findViewById(R.id.add_rule_another_field);
        addRuleFieldsLayout = (LinearLayout) findViewById(R.id.add_rule_fields);

        ruleFields = new ArrayList<>();
        addRuleField();
    }

    private void setupCustomActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.custom_actionbar_add_rule, null);

        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void addRuleField() {
        RuleFieldView ruleField = new RuleFieldView(this, ruleFields, addRuleFieldsLayout);
        ruleFields.add(ruleField);
        addRuleFieldsLayout.addView(ruleField.getView());

        addRuleAnotherFieldButton.setVisibility(View.INVISIBLE);
    }

    public void addRule(View view) {
        //TODO: insert rule to database
    }

    public void addAdditionalRuleField(View view) {
        addRuleField();
    }

    public void close(View view) {
        showDiscardChangesDialog();
    }

    @Override
    public void onBackPressed() {
        showDiscardChangesDialog();
    }

    private boolean hasChanges() {
        boolean hasChange = false;
        if (!nameEditText.getText().toString().isEmpty()) {
            hasChange = true;
        } else {
            for (RuleFieldView ruleField : ruleFields) {
                if (!ruleField.getNumberText().isEmpty()) {
                    hasChange = true;
                    break;
                }
            }
        }
        return hasChange;
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
