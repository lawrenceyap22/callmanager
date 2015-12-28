package ph.intrepidstream.callmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ph.intrepidstream.callmanager.service.CallManageService;

public class BootUpReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, CallManageService.class);
        context.startService(newIntent);
    }
}
