package ph.intrepidstream.callmanager.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import ph.intrepidstream.callmanager.R;
import ph.intrepidstream.callmanager.util.AppGlobal;
import ph.intrepidstream.callmanager.util.RuleState;

public class DialogActivity extends Activity {

    public static final String EXTRA_OPERATOR_NAME = "ph.intrepidstream.callmanager.ui.OPERATOR_NAME";
    public static final String EXTRA_DIALOG_TYPE = "ph.intrepidstream.callmanager.ui.DIALOG_TYPE";
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1;

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
                            attemptCall();
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

    private void attemptCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    showRequestPermissionRationale(this);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
                }
                return;
            }
        }
        proceedCall();

    }

    private void showRequestPermissionRationale(final Activity thisActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_denied_title)
                .setMessage(R.string.permission_denied_message)
                .setCancelable(false)
                .setPositiveButton(R.string.permission_denied_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
                        dialog.cancel();
                    }
                });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        proceedCall();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            showRequestPermissionRationale(this);
                        }
                    }
                }
            }
        }
    }
}
