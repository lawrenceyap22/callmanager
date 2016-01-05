package ph.intrepidstream.callmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.service.CallManageService;
import ph.intrepidstream.callmanager.ui.adapter.ExpandableBlockListViewAdapter;

public class MainActivity extends AppCompatActivity {
    private boolean isServiceEnabled;

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
        View customView = inflater.inflate(R.layout.custom_actionbar, null);

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
        expandableListView.setAdapter(new ExpandableBlockListViewAdapter(this));
    }

    private void setupFloatingActionButton(FloatingActionButton floatingActionButton) {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addRuleIntent = new Intent(v.getContext(), AddRuleActivity.class);
                startActivity(addRuleIntent);
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
    protected void onStop() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.call_manage_service_key), isServiceEnabled);
        editor.apply();
        super.onStop();
    }
}
