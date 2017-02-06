package com.example.vincent.notstealingyoursmsapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vincent on 06/02/2017.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            String smsAddress = "";


            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                smsAddress = smsMessage.getOriginatingAddress();

                long timeMillis = smsMessage.getTimestampMillis();

                Date date = new Date(timeMillis);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                String dateText = format.format(date);

                smsMessageStr += address +" at "+"\t"+ dateText + "\n";
                smsMessageStr += smsBody + "\n";
            }

            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            if(smsMessageStr.contains("hello")){
                Log.e("SMS","ABORTED");
                ContentValues values = new ContentValues();
                values.put("address", smsAddress);
                values.put("body", smsMessageStr+"absolutely not changed SMS");

                context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                abortBroadcast();
            }

            //this will update the UI with message
            ReceiveSmsActivity inst = ReceiveSmsActivity.instance();
            inst.updateList(smsMessageStr);
        }
    }
}
