package ph.intrepidstream.callmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
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

    private Country selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        selectedCountry = Country.valueOf(getIntent().getStringExtra(MainActivity.EXTRA_SELECTED_COUNTRY));

        ActionBar actionBar = getSupportActionBar();
        initCustomActionBar(actionBar);

        ListView listView = (ListView) findViewById(R.id.activity_country_list);
        setupCountryExpandableList(listView);
    }

    private void setupCountryExpandableList(final ListView listView) {
        Country[] countryArray = Arrays.copyOfRange(Country.values(), 1, Country.values().length);
        List<Country> countries = Arrays.asList(countryArray);

        listView.setAdapter(new CountryListViewAdapter(this, countries));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = (Country) listView.getItemAtPosition(position);
            }
        });

        //TODO set selected country
        int selectedIndex = countries.indexOf(selectedCountry);
        if (selectedIndex >= 0) {
//            listView.requestFocusFromTouch();
            listView.setItemChecked(selectedIndex, true);
        }
    }

    private void initCustomActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.actionbar_country_custom, null);
        AppCompatImageButton cancelButton = (AppCompatImageButton) view.findViewById(R.id.select_country_cancel);
        if (selectedCountry == Country.NONE) {
            cancelButton.setVisibility(View.GONE);
        }

        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    public void save(View view) {
        if (selectedCountry != Country.NONE) {
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
