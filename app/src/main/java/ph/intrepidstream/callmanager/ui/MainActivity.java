package ph.intrepidstream.callmanager.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.ImageView;

import ph.intrepidstream.callmanager.BuildConfig;
import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.dao.RuleDao;
import ph.intrepidstream.callmanager.dao.impl.RuleDaoImpl;
import ph.intrepidstream.callmanager.db.DBHelper;
import ph.intrepidstream.callmanager.service.CallManageService;
import ph.intrepidstream.callmanager.ui.adapter.ExpandableBlockListViewAdapter;
import ph.intrepidstream.callmanager.util.Country;
import ph.intrepidstream.callmanager.util.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_EDIT_RULE = "ph.intrepidstream.callmanager.ui.EDIT_RULE";
    public final static String EXTRA_SELECTED_COUNTRY = "ph.intrepidstream.callmanager.ui.SELECTED_COUNTRY";
    public final static int ADD_EDIT_RULE_REQUEST = 1;
    public final static int SELECT_COUNTRY_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1;

    private final String TAG = MainActivity.class.getName();

    private boolean isServiceEnabled;
    private ExpandableBlockListViewAdapter rulesAdapter;
    private int lastExpandedGroupInRulesAdapter;
    private RuleDao ruleDao;
    private Country country;
    private ImageView countryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        country = PreferenceManager.getInstance(this).getCountry();

        if (country == Country.NONE) {
            Intent intent = new Intent(this, CountryActivity.class);
            startActivityForResult(intent, SELECT_COUNTRY_REQUEST);
        }
        ruleDao = RuleDaoImpl.getInstance();

        ActionBar actionBar = getSupportActionBar();
        setupCustomActionBar(actionBar);

        SwitchCompat serviceEnabledSwitch = (SwitchCompat) findViewById(R.id.action_bar_switch);
        setupServiceEnabledSwitch(serviceEnabledSwitch);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.activity_main_expandable_list);
        setupExpandableListView(expandableListView);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.activity_main_floating_action_button);
        setupFloatingActionButton(floatingActionButton);

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
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.actionbar_background_white, null));

        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.actionbar_custom, null);
        countryImageView = (ImageView) customView.findViewById(R.id.action_bar_select_country);
        if (country != Country.NONE) {
            int id = getResources().getIdentifier(country.name().toLowerCase(), "drawable", getPackageName());
            countryImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
        }

        countryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent countryIntent = new Intent(MainActivity.this, CountryActivity.class);
                startActivityForResult(countryIntent, SELECT_COUNTRY_REQUEST);
            }
        });
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

        boolean isServiceEnabled = PreferenceManager.getInstance(this).isServiceEnabled();
        serviceEnabledSwitch.setChecked(isServiceEnabled);
    }

    private void setupExpandableListView(final ExpandableListView expandableListView) {
        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        lastExpandedGroupInRulesAdapter = -1;
        rulesAdapter = new ExpandableBlockListViewAdapter(this, ruleDao.retrieveRulesByCountry(db, country));
        expandableListView.setAdapter(rulesAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedGroupInRulesAdapter != -1 && lastExpandedGroupInRulesAdapter != groupPosition) {
                    expandableListView.collapseGroup(lastExpandedGroupInRulesAdapter);
                }
                lastExpandedGroupInRulesAdapter = groupPosition;
            }
        });
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
        PreferenceManager.getInstance(this).setServiceEnabled(enable);
        Intent intent = new Intent(this, CallManageService.class);
        if (enable) {
            askPermission();
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
            }
        }
    }

    private void showRequestPermissionRationale(final Activity thisActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_denied_title)
                .setMessage(R.string.permission_denied_message)
                .setCancelable(false)
                .setPositiveButton(R.string.permission_denied_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.permission_denied_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_EDIT_RULE_REQUEST && resultCode == RESULT_OK) {
            DBHelper dbHelper = DBHelper.getInstance(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            rulesAdapter.setRules(ruleDao.retrieveRulesByCountry(db, country));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rulesAdapter.notifyDataSetChanged();
                }
            });
        } else if (requestCode == SELECT_COUNTRY_REQUEST && resultCode == RESULT_OK) {
            String newCountryName = data.getStringExtra(EXTRA_SELECTED_COUNTRY);
            Country newCountry = Country.valueOf(newCountryName);
            if (country != newCountry) {
                PreferenceManager.getInstance(this).setCountry(newCountry);
                country = newCountry;
                countryImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getResources().getIdentifier(country.name().toLowerCase(), "drawable", getPackageName()), null));
                DBHelper dbHelper = DBHelper.getInstance(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                rulesAdapter.setRules(ruleDao.retrieveRulesByCountry(db, country));
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
    protected void onDestroy() {
        if (!isServiceEnabled) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Closing Database");
            }
            DBHelper.getInstance(this).close();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            showRequestPermissionRationale(this);
                        }
                    }
                }
            }
        }
    }
}