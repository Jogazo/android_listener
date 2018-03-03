package be.unleashed.ringring.androidtelcolistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jogazo on 3/03/18.
 */

public class MySMSMailBox extends BroadcastReceiver {
    public static final String tag = "androidtelcolistener";

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();

        if (action.equals("android.provider.Telephony.SMS_RECEIVED")){
            StringBuilder sb = new StringBuilder();

            final Bundle bundle = intent.getExtras();

            if (bundle == null) return;

            final Object[] pdus = (Object[]) bundle.get("pdus");
            ArrayList<SmsMessage> messages = new ArrayList<SmsMessage>();
            for (Object pdu : pdus) {
                // Hopefully, the pdus are in the order that the parts of the SMS message should be
                messages.add(SmsMessage.createFromPdu((byte[])pdu));
            }

            String messageBody = new String();
            for (int index = 0; index < messages.size(); index ++) {
                messageBody += messages.get(index).getMessageBody();
            }

            String phoneNumber = messages.get(0).getOriginatingAddress();
            sb.append("SMS received from\t" + phoneNumber.toString());

            if (messageBody != null) {
                sb.append("\twith content:\t" + messageBody.toString());
            }

            Log.w(tag, sb.toString());
        }

    }
}
