package ph.intrepidstream.callmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.db.CallManagerDatabaseContract;
import ph.intrepidstream.callmanager.db.DBHelper;
import ph.intrepidstream.callmanager.dto.Condition;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.service.CallManageService;
import ph.intrepidstream.callmanager.ui.adapter.ExpandableBlockListViewAdapter;
import ph.intrepidstream.callmanager.util.ConditionLookup;
import ph.intrepidstream.callmanager.util.RuleState;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_EDIT_RULE = "ph.intrepidstream.callmanager.ui.EDIT_RULE";
    public final static int ADD_EDIT_RULE_REQUEST = 1;

    private final String TAG = MainActivity.class.getName();

    private boolean isServiceEnabled;
    private ExpandableBlockListViewAdapter rulesAdapter;

    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        setupCustomActionBar(actionBar);

        SwitchCompat serviceEnabledSwitch = (SwitchCompat) findViewById(R.id.action_bar_switch);
        setupServiceEnabledSwitch(serviceEnabledSwitch);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.activity_main_expandable_list);
        setupExpandableListView(expandableListView);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.activity_main_floating_action_button);
        setupFloatingActionButton(floatingActionButton);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Return false so that the three-dot menu will not show up. Menu is just
        // "invisible", and is there just in case a need for it arises in the future.
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private void setupCustomActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.actionbar_custom, null);

        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void setupServiceEnabledSwitch(SwitchCompat serviceEnabledSwitch) {
        serviceEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableService(isChecked);
            }
        });

        boolean isServiceEnabled = getServiceEnabledStatus();
        serviceEnabledSwitch.setChecked(isServiceEnabled);
    }

    private boolean getServiceEnabledStatus() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(getString(R.string.call_manage_service_key), false);
    }

    private void setupExpandableListView(ExpandableListView expandableListView) {
        rulesAdapter = new ExpandableBlockListViewAdapter(this, retrieveRules());
        expandableListView.setAdapter(rulesAdapter);
    }

    private List<Rule> retrieveRules() {
        List<Rule> rules = new ArrayList<>();
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {CallManagerDatabaseContract.RuleEntry._ID, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE, CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED};
        String sortOrder = CallManagerDatabaseContract.RuleEntry._ID;
        Cursor cursor = db.query(CallManagerDatabaseContract.RuleEntry.TABLE_NAME, columns, null, null, null, null, sortOrder);

        if (cursor.moveToFirst()) {
            do {
                Rule rule = new Rule();
                rule.setId(cursor.getLong(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry._ID)));
                rule.setName(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_NAME)));
                rule.setState(RuleState.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_STATE))));
                rule.setIsAppGenerated(cursor.getInt(cursor.getColumnIndex(CallManagerDatabaseContract.RuleEntry.COLUMN_NAME_APP_GENERATED)) != 0);
                rule.setConditions(retrieveConditions(rule.getId(), db));
                rules.add(rule);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Retrieved Rule: " + rule.toString());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rules;
    }

    private List<Condition> retrieveConditions(Long ruleId, SQLiteDatabase db) {
        List<Condition> conditions = new ArrayList<>();
        String[] columns = {CallManagerDatabaseContract.ConditionEntry._ID, CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID, CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_LOOKUP, CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_NUMBER};
        String whereClause = CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID + "=?";
        String[] whereClauseArgs = {ruleId.toString()};
        String sortOrder = CallManagerDatabaseContract.ConditionEntry._ID;
        Cursor cursor = db.query(CallManagerDatabaseContract.ConditionEntry.TABLE_NAME, columns, whereClause, whereClauseArgs, null, null, sortOrder);

        if (cursor.moveToFirst()) {
            do {
                Condition condition = new Condition();
                condition.setId(cursor.getLong(cursor.getColumnIndex(CallManagerDatabaseContract.ConditionEntry._ID)));
                condition.setRuleId(cursor.getLong(cursor.getColumnIndex(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_RULE_ID)));
                condition.setLookup(ConditionLookup.valueOf(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_LOOKUP))));
                condition.setNumber(cursor.getString(cursor.getColumnIndex(CallManagerDatabaseContract.ConditionEntry.COLUMN_NAME_NUMBER)));
                conditions.add(condition);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Retrieved Condition: " + condition.toString());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return conditions;
    }

    private void setupFloatingActionButton(FloatingActionButton floatingActionButton) {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addRuleIntent = new Intent(v.getContext(), AddRuleActivity.class);
                startActivityForResult(addRuleIntent, ADD_EDIT_RULE_REQUEST);
            }
        });
    }

    private void enableService(boolean enable) {
        isServiceEnabled = enable;
        Intent intent = new Intent(this, CallManageService.class);
        if (enable) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_EDIT_RULE_REQUEST) {
            if (resultCode == RESULT_OK) {
                rulesAdapter.setRules(retrieveRules());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rulesAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.call_manage_service_key), isServiceEnabled);
        editor.apply();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Closing Database");
        }
        DBHelper.getInstance(this).close();
        super.onDestroy();
    }
}