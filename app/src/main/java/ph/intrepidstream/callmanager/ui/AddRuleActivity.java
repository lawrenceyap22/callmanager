package ph.intrepidstream.callmanager.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.R;

public class AddRuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);

        ActionBar actionBar = getSupportActionBar();
        setupCustomActionBar(actionBar);

        Spinner spinner = (Spinner) findViewById(R.id.add_rule_spinner1);
        setSpinnerList(spinner);
    }

    private void setupCustomActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.custom_actionbar_add_rule, null);

        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void setSpinnerList(Spinner spinner) {
        List<String> conditions = new ArrayList<>();
        conditions.add("starts with");
        conditions.add("equals");
        conditions.add("not starts with");
        conditions.add("not equals");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conditions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void close(View view) {
        finish();
    }

    public void addRule(View view) {
        //TODO: insert rule to database
    }

    public void addRuleField(View view) {
        //TODO: add field
    }
}
