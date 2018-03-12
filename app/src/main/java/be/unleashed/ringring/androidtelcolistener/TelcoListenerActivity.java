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
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TelcoListenerActivity extends AppCompatActivity {
    public static final String tag = MySMSMailBox.tag;
    private TextView telMgrOutput;
    private EditText getInput;

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
        this.getInput = (EditText) findViewById(R.id.get_input);

//        String output = getHttpResponse(getInput.getText().toString());
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

    /**
     * Perform an HTTP GET with HttpUrlConnection.
     *
     * @param location
     * @return
     */
    private String getHttpResponse(String location) {
        String result = null;
        URL url = null;
        Log.d(tag, " " + " location = " + location);

        try {
            url = new URL(location);
            Log.d(tag, " " + " url = " + url);
        } catch (MalformedURLException e) {
            Log.e(tag, " " + " " + e.getMessage());
        }

        if (url != null) {
            try {
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String inputLine;

                int lineCount = 0; // limit the lines for the example
                while ((lineCount < 10) && ((inputLine = in.readLine()) != null)) {
                    lineCount++;
                    Log.v(tag, " " + " inputLine = " + inputLine);
                    result += "\n" + inputLine;
                }

                in.close();
                urlConn.disconnect();

            } catch (IOException e) {
                Log.e(tag, " " + " " + e.getMessage());
            }
        } else {
            Log.e(tag, " " + " url NULL");
        }
        return result;
    }

}
