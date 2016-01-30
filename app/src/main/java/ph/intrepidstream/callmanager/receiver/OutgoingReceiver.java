package ph.intrepidstream.callmanager.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Iterator;
import java.util.List;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.dao.RuleDao;
import ph.intrepidstream.callmanager.dao.impl.RuleDaoImpl;
import ph.intrepidstream.callmanager.db.DBHelper;
import ph.intrepidstream.callmanager.dto.Rule;
import ph.intrepidstream.callmanager.ui.DialogActivity;
import ph.intrepidstream.callmanager.ui.MainActivity;
import ph.intrepidstream.callmanager.util.AppGlobal;
import ph.intrepidstream.callmanager.util.RuleState;

public class OutgoingReceiver extends BroadcastReceiver {

    private static final int BLOCK_NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            phoneNumber = replaceCountryCode(phoneNumber);
            RuleState ruleState = determineRuleState(context, phoneNumber);
            if (ruleState == RuleState.WARN) {
                if (!AppGlobal.shouldProceedCall) {
                    setResultData(null);
                    showWarningDialog(context, phoneNumber);
                } else {
                    AppGlobal.shouldProceedCall = false;
                }
            } else if (ruleState == RuleState.BLOCK) {
                setResultData(null);
                showBlockedNotification(context, phoneNumber);
            }
        }
    }

    private String replaceCountryCode(String phoneNumber) {
        if (phoneNumber.startsWith("+63")) {
            phoneNumber = phoneNumber.replaceFirst("\\+63", "0");
        }
        return phoneNumber;
    }

    private RuleState determineRuleState(Context context, String phoneNumber) {
        RuleState ruleState = RuleState.OFF;
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        RuleDao ruleDao = RuleDaoImpl.getInstance();
        List<Rule> rules = ruleDao.retrieveRules(db);
        Iterator<Rule> ruleIterator = rules.iterator();
        Rule rule;
        while (ruleState == RuleState.OFF && ruleIterator.hasNext()) {
            rule = ruleIterator.next();
            if (rule.isIncluded(phoneNumber)) {
                ruleState = rule.getState();
            }
        }
        return ruleState;
    }

    private void showWarningDialog(Context context, String phoneNumber) {
        Intent dialogIntent = new Intent(context, DialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
        context.startActivity(dialogIntent);
    }

    private void showBlockedNotification(Context context, String phoneNumber) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        String contentText = context.getString(R.string.block_notification_content, phoneNumber);
        builder.setSmallIcon(android.R.drawable.ic_btn_speak_now) //TODO change icon notif
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setAutoCancel(true);

        Intent mainIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainIntent);

        PendingIntent mainPendingIntent = stackBuilder.getPendingIntent(MainActivity.PENDING_INTENT_REQUEST, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(mainPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(BLOCK_NOTIFICATION_ID, builder.build());
    }
}
