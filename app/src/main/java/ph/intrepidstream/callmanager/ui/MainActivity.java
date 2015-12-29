package ph.intrepidstream.callmanager.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.ui.fragment.BlockListFragment;
import ph.intrepidstream.callmanager.ui.fragment.CMPagerAdapter;
import ph.intrepidstream.callmanager.ui.fragment.CallLogsFragment;
import ph.intrepidstream.callmanager.ui.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CMPagerAdapter pagerAdapter = new CMPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new CallLogsFragment(), getString(R.string.call_logs_tab_title));
        pagerAdapter.addFragment(new BlockListFragment(), getString(R.string.block_list_tab_title));
        pagerAdapter.addFragment(new SettingsFragment(), getString(R.string.settings_tab_title));

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id==R.id.action_settings || super.onOptionsItemSelected(item);
    }



}
