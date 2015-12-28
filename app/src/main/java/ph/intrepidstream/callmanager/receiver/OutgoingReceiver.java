package ph.intrepidstream.callmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ph.intrepidstream.callmanager.ui.DialogActivity;
import ph.intrepidstream.callmanager.util.AppGlobal;

public class OutgoingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            if(phoneNumber.equals("09233733518")){
                if(!AppGlobal.shouldProceedCall){
                    setResultData(null);
                    showWarningDialog(context, phoneNumber);
                }else{
                    AppGlobal.shouldProceedCall = false;
                }
            }
        }
    }

    private void showWarningDialog(Context context, String phoneNumber){
        Intent dialogIntent = new Intent(context, DialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
        context.startActivity(dialogIntent);
    }
}
