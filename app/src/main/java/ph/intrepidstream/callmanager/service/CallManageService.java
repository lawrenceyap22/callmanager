package ph.intrepidstream.callmanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.receiver.OutgoingReceiver;
import ph.intrepidstream.callmanager.ui.MainActivity;

public class CallManageService extends Service {

    private final int NOTIFICATION_ID = 1;

    private OutgoingReceiver outgoingReceiver;
    private IntentFilter intentFilter;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        outgoingReceiver = new OutgoingReceiver();
        intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(outgoingReceiver, intentFilter);
        createOngoingNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createOngoingNotification() {
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_content))
                .setOngoing(true);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(outgoingReceiver);
        notificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
