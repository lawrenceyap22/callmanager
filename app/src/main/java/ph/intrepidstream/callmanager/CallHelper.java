package ph.intrepidstream.callmanager;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * Helper class to detect incoming and outgoing calls.
 * @author Moskvichev Andrey V.
 *
 */
public class CallHelper {
    /**
     * Broadcast receiver to detect the outgoing calls.
     */

    public class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, final Intent intent) {
            final String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            builder.setTitle("WARNING");
            builder.setMessage("This number is in your blocked numbers list. Are you sure you want to proceed with this call?");
            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    try {
                        ctx.unregisterReceiver(outgoingReceiver);
                        Intent callIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(callIntent);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ctx.registerReceiver(outgoingReceiver, intentFilter);
                            }
                        }, 100);
                    } catch (ActivityNotFoundException activityException) {
                        Log.e("Calling a Phone Number", "Call failed", activityException);
                    } catch (SecurityException securityException) {
                        Log.e("Calling a Phone Number", "Call failed", securityException);
                    }
                    dialog.dismiss();
                }

            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            if(number.equals("09178599670")) {
                AlertDialog alert = builder.create();
                alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alert.show();
                setResultData(null);
            }
        }

    }

    private Context ctx;

    private OutgoingReceiver outgoingReceiver;

    private AlertDialog.Builder builder;

    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);

    public CallHelper(Context ctx) {
        this.ctx = ctx;

        outgoingReceiver = new OutgoingReceiver();
        builder = new AlertDialog.Builder(ctx);
    }

    /**
     * Start calls detection.
     */
    public void start() {
        intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        ctx.registerReceiver(outgoingReceiver, intentFilter);
    }

    /**
     * Stop calls detection.
     */
    public void stop() {
        ctx.unregisterReceiver(outgoingReceiver);
    }

}
