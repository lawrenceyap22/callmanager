package ph.intrepidstream.callmanager.util;

import android.content.Context;
import android.content.SharedPreferences;

import ph.intrepidstream.callmanager.R;

public class PreferenceManager {

    private static PreferenceManager instance = null;
    private Context context;
    private SharedPreferences sharedPreferences;

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    private PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public Country getCountry() {
        String selected = sharedPreferences.getString(context.getString(R.string.call_manager_country), Country.NONE.name());
        return Country.valueOf(selected);
    }

    public boolean isServiceEnabled() {
        return sharedPreferences.getBoolean(context.getString(R.string.call_manage_service_key), false);
    }

    public void setCountry(Country country) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.call_manager_country), country.name());
        editor.apply();
    }

    public void setServiceEnabled(boolean isServiceEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.call_manage_service_key), isServiceEnabled);
        editor.apply();
    }


}
