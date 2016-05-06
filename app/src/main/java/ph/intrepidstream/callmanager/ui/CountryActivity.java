package ph.intrepidstream.callmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.ui.adapter.CountryListViewAdapter;
import ph.intrepidstream.callmanager.util.Country;

public class CountryActivity extends AppCompatActivity {

    private final String TAG = CountryActivity.class.getName();

    private List<Country> countries;
    private ListView listView;
    private Country selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        ActionBar actionBar = getSupportActionBar();
        initCustomActionBar(actionBar);

        setupCountryExpandableList();
    }

    private void setupCountryExpandableList() {
        Country[] countryArray = Arrays.copyOfRange(Country.values(), 1, Country.values().length);
        countries = Arrays.asList(countryArray);

        listView = (ListView) findViewById(R.id.activity_country_list);
        listView.setAdapter(new CountryListViewAdapter(this, countries));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = (Country) listView.getAdapter().getItem(position);
            }
        });
    }

    private void initCustomActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.actionbar_country_custom, null);
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    public void save(View view) {
        if (selectedCountry != null) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.EXTRA_SELECTED_COUNTRY, selectedCountry.name());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }


}
