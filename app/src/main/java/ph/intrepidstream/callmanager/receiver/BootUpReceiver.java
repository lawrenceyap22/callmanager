package ph.intrepidstream.callmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.service.CallManageService;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (sharedPref.getBoolean(context.getString(R.string.call_manage_service_key), false)) {
            Intent newIntent = new Intent(context, CallManageService.class);
            context.startService(newIntent);
        }
    }
}
