package ph.intrepidstream.callmanager.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.util.AppGlobal;
import ph.intrepidstream.callmanager.util.RuleState;

public class DialogActivity extends Activity {

    public static final String EXTRA_OPERATOR_NAME = "ph.intrepidstream.callmanager.ui.OPERATOR_NAME";
    public static final String EXTRA_DIALOG_TYPE = "ph.intrepidstream.callmanager.ui.DIALOG_TYPE";

    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String operatorName = intent.getStringExtra(EXTRA_OPERATOR_NAME);
        String type = intent.getStringExtra(EXTRA_DIALOG_TYPE);
        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (RuleState.WARN.toString().equals(type)) {
            builder.setTitle(R.string.app_name)
                    .setMessage(getString(R.string.outgoing_warning_message, operatorName))
                    .setCancelable(false)
                    .setPositiveButton(R.string.outgoing_warning_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            proceedCall();
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
        } else {
            builder.setTitle(R.string.app_name)
                    .setMessage(getString(R.string.outgoing_block_message, operatorName))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressWarnings("ResourceType")
    private void proceedCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppGlobal.shouldProceedCall = true;
        startActivity(callIntent);
    }

}
