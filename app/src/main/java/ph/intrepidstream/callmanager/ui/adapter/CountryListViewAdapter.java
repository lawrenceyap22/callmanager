package ph.intrepidstream.callmanager.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.util.Country;

public class CountryListViewAdapter extends BaseAdapter {

    private Context context;
    private List<Country> countries;

    public CountryListViewAdapter(Context context, List<Country> countries) {
        this.context = context;
        this.countries = countries;
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_country, parent, false);
        }

        Country country = (Country) getItem(position);
        TextView label = (TextView) convertView.findViewById(R.id.country_label);
        label.setText(country.toString());
        label.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getIdentifier(country.name().toLowerCase(), "drawable", context.getPackageName()), 0, 0, 0);

        return convertView;
    }
}