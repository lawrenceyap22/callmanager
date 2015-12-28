package ph.intrepidstream.callmanager.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import ph.intrepidstream.callmanager.receiver.OutgoingReceiver;

public class CallManageService extends Service {

    private OutgoingReceiver outgoingReceiver;
    private IntentFilter intentFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        outgoingReceiver = new OutgoingReceiver();
        intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(outgoingReceiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(outgoingReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
