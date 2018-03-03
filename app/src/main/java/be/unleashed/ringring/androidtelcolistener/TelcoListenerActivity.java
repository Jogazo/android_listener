package be.unleashed.ringring.androidtelcolistener;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

public class TelcoListenerActivity extends AppCompatActivity {
    public static final String tag = MySMSMailBox.tag;
    private TextView telMgrOutput;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(tag, "TelcoListenerActivity onCreate");
        boolean close_no_permissions = false;
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_telco_listener);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(tag, "ERROR: enable RECEIVE_SMS permission on device");
            close_no_permissions = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(tag, "ERROR: enable READ_PHONE_STATE permission on device");
            close_no_permissions = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(tag, "ERROR: enable READ_SMS permission on device");
            close_no_permissions = true;
        }

        if (close_no_permissions){
            finish();
        }

        this.telMgrOutput = (TextView) findViewById(R.id.telmgroutput);
    }

    @Override
    public void onStart() {
        super.onStart();
        final TelephonyManager telMgr = (TelephonyManager) this.getSystemService(
                        Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener =
                new PhoneStateListener() {
                    public void onCallStateChanged(
                            int state, String incomingNumber) {
                        telMgrOutput.setText(getTelephonyOverview(telMgr));
                    }
                };
        telMgr.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        String telephonyOverview = this.getTelephonyOverview(telMgr);
        this.telMgrOutput.setText(telephonyOverview);
    }

    public String getTelephonyOverview(TelephonyManager telMgr) {
        String callStateString = "NA";
        int callState = telMgr.getCallState();

        switch (callState) {
            case TelephonyManager.CALL_STATE_IDLE:
                callStateString = "IDLE";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                callStateString = "OFFHOOK";
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                callStateString = "RINGING";
                break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("telMgr - call state = " + callStateString);

        Log.w(tag, "callStateString: " + callStateString);
        return sb.toString();
    }

}
