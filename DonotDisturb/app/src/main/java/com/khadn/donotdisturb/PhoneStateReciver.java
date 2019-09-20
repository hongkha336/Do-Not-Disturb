package com.khadn.donotdisturb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;

public class PhoneStateReciver extends BroadcastReceiver {


    private String KEY_ENABLE;
    private String KEY_TERMINATE;
    private String KEY_SMS;
    private MyDatabaseHelper myDatabaseHelper;
    private String MESSAGE;
    private String TIME_DISTURB;
    private DateTimeHelper dateTimeHelper;
    private String TIME_NOW;
    @Override
    public void onReceive(Context context, Intent intent) {

        //Get the setting in database
        dateTimeHelper = new DateTimeHelper();
        myDatabaseHelper = new MyDatabaseHelper(context);
        KEY_ENABLE = myDatabaseHelper.getSettingValue(SystemKey.IS_ENABLE);
        KEY_TERMINATE = myDatabaseHelper.getSettingValue(SystemKey.IS_TERMINATE);
        KEY_SMS = myDatabaseHelper.getSettingValue(SystemKey.IS_SEND_MESSAGE);
        TIME_DISTURB = myDatabaseHelper.getSettingValue(SystemKey.TIME_DISTURB);
        MESSAGE = myDatabaseHelper.getSettingValue(SystemKey.MESSAGE_TEMPLATE);
        TIME_NOW = String.valueOf(dateTimeHelper.convertToDateTime(dateTimeHelper.getDateTime()).getMillis());

        // if check the condition of working
        if(KEY_ENABLE.equals("true")){


        if(dateTimeHelper.compareDateTime(TIME_DISTURB,TIME_NOW) == 1)
        {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(context," Terminate incoming call from " +number,Toast.LENGTH_SHORT).show();
            if(KEY_TERMINATE.equals("true")) {
                terminateCall(context);
            }
            if(KEY_SMS.equals("true"))
            {
                sendMessage(number,MESSAGE);
            }
        }
        }
        else
        {
         myDatabaseHelper.updateSetting(SystemKey.IS_ENABLE,"false");
        }
        }
    }


    // Terminate a phone call
    private  void terminateCall(Context context ) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception ex) { // Many things can go wrong with reflection calls

            String error = ex.toString();
            Toast.makeText(context, "error: " + error, Toast.LENGTH_LONG).show();
        }

    }


    // send message without opening message app
    private void sendMessage(String srcNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(srcNumber, null, message, null, null);
    }
}
