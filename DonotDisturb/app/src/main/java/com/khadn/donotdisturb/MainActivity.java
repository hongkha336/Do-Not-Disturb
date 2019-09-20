package com.khadn.donotdisturb;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.format.*;
import org.joda.time.*;

public class MainActivity extends AppCompatActivity {


    // start declare entity
    Button btnRing, btnVib, btnSil, btnSetTime, btnEdit;
    Button btn15Min, btn30Min, btn1Hour, btn2Hour, btn4Hour, btn8Hour, btn12Hour, btn24Hour, btn48Hour;
    TextView message;
    Calendar calendar;
    TimePickerDialog timepickerdialog;
    Switch sw_Terminate;
    Switch sw_SendMessage;
    Switch sw_Enable;
    // end declare entity

    //declare the other variable
    int CalendarHour, CalendarMinute;
    String TimeSet, format;
    //end declare

    //declare model variable
    MyDatabaseHelper myDatabaseHelper;
    DateTimeHelper dateTimeHelper;
    //end declare


    //Declare SETTING
    String st_Terminate;
    String st_SendMessage;
    String st_Enable;
    String TIME_NOW;
    String TIME_DISTURB;
    String PREVIOUS_STATE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // check permission and get setting
        checkPermission();
        myDatabaseHelper = new MyDatabaseHelper(MainActivity.this);
        myDatabaseHelper.setDefaulSettingIfNeed();
        dateTimeHelper = new DateTimeHelper();

        TIME_NOW = String.valueOf(dateTimeHelper.convertToDateTime(dateTimeHelper.getDateTime()).getMillis());
        TIME_DISTURB = myDatabaseHelper.getSettingValue(SystemKey.TIME_DISTURB);
        PREVIOUS_STATE  = myDatabaseHelper.getSettingValue(SystemKey.PRIVIOUS_STATE);
        if(PREVIOUS_STATE.equals(""))
        {
            boolean isRing = checkRingerIsOn(getApplicationContext());
            PREVIOUS_STATE = String.valueOf(isRing);
            myDatabaseHelper.updateSetting(SystemKey.PRIVIOUS_STATE,String.valueOf(isRing));
        }

        //try to turn off the block function when open the app
        try{
            if(dateTimeHelper.compareDateTime(TIME_DISTURB,TIME_NOW) <= 0)
            {
                myDatabaseHelper.updateSetting(SystemKey.IS_ENABLE,"false");

            }
        }
        catch (Exception ex)
        {

        }

        getPermission();
        mappingButton();
        setListenerForButton();
        mappingBtnSettime();
        mappingQuickSetbutton();
        setListenerForQuickset();
        getSetting();
        mappingSw();
        setListenerForSw();
        MappingMessage();

        //Check the state of the phone
        if(st_Enable.equals("false"))
        {
            boolean isRing = checkRingerIsOn(getApplicationContext());
            PREVIOUS_STATE = String.valueOf(isRing);
            myDatabaseHelper.updateSetting(SystemKey.PRIVIOUS_STATE,String.valueOf(isRing));
        }


    }



    /***********************************/
    //OTHER FUNCTION FUNCTION
    /***********************************/
    //get the permission for action phone state
    private  void getPermission()
    {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (!nm.isNotificationPolicyAccessGranted()) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
            }
        }
    }
    // set the listener for the button. action set the Ringer mode - silent normal - vibrate
    private void setListenerForButton()
    {
        btnRing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingerMode(MainActivity.this,AudioManager.RINGER_MODE_NORMAL);
            }
        });


        btnSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingerMode(MainActivity.this,AudioManager.RINGER_MODE_SILENT);
            }
        });

        btnVib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingerMode(MainActivity.this,AudioManager.RINGER_MODE_VIBRATE);
            }
        });
    }
    // set the RingerMode to the
    private void setRingerMode(Context context,int mode){
        NotificationManager nm = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        AudioManager audioManager = (AudioManager)context.getSystemService(AUDIO_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && nm.isNotificationPolicyAccessGranted())
            audioManager.setRingerMode(mode);
    }
    //get value of setting

    private  void getSetting()
    {
        st_SendMessage = myDatabaseHelper.getSettingValue(SystemKey.IS_SEND_MESSAGE);
        st_Terminate = myDatabaseHelper.getSettingValue(SystemKey.IS_TERMINATE);
        st_Enable = myDatabaseHelper.getSettingValue(SystemKey.IS_ENABLE);
    }

    /***********************************/
    //MAPPING FUNCTION
    /***********************************/

    //mapping the other button
    private  void mappingQuickSetbutton()
    {
         btn15Min = (Button) findViewById(R.id.btn15Min);
         btn30Min = (Button) findViewById(R.id.btn30Min);
         btn1Hour = (Button) findViewById(R.id.btn1Hour);
         btn2Hour = (Button) findViewById(R.id.btn2Hours);
         btn4Hour = (Button) findViewById(R.id.btn4Hours);
         btn8Hour = (Button) findViewById(R.id.btn8Hours);
         btn12Hour = (Button) findViewById(R.id.btn12Hours);
         btn24Hour = (Button) findViewById(R.id.btn24Hours);
         btn48Hour = (Button) findViewById(R.id.btn48Hours);
    }
    //mapping button settime and the listenter
    private  void mappingBtnSettime()
    {
        btnSetTime = (Button) findViewById(R.id.btnSetTime);
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
                CalendarMinute = calendar.get(Calendar.MINUTE);
                timepickerdialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        if (hourOfDay == 0) {

                            hourOfDay += 12;

                            format = "AM";
                        } else if (hourOfDay == 12) {

                            format = "PM";

                        } else if (hourOfDay > 12) {

                            hourOfDay -= 12;

                            format = "PM";

                        } else {

                            format = "AM";
                        }
                        String min = String.valueOf(minute);
                        String hour = String.valueOf(hourOfDay);
                        if(min.length() < 2)
                            min = "0" + min;
                        if(hour.length()<2)
                            hour = "0" + hour;
                        if(format.equals("PM"))
                            hour = String.valueOf(Integer.valueOf(hour) + 12);
                        else
                        {
                            if(hour.equals("12"))
                                hour ="00";
                        }

                        TimeSet = dateTimeHelper.getDate() +" "+ hour + ":" + min  + ":"+"00" ;
                        setDisturb(TimeSet);

                    }
                }, CalendarHour, CalendarMinute, false);
                timepickerdialog.show();

            }
        });
    }
    // mapping with the button which set the state of the phone
    private  void mappingButton()
    {
        btnRing = (Button) findViewById(R.id.btnNormal);
        btnVib = (Button) findViewById(R.id.btnVibrate);
        btnSil = (Button) findViewById(R.id.btnSilent);
    }
    //mapping switch
    private void mappingSw()
    {
        sw_SendMessage = (Switch) findViewById(R.id.switch3);
        sw_Terminate = (Switch) findViewById(R.id.sw_terminate);
        sw_Enable = (Switch) findViewById(R.id.sw_enable);
        setCheck();
    }
    //mapping value for switch
    private  void setCheck()
    {
        boolean isTer = Boolean.valueOf(st_Terminate);
        boolean isSendMe = Boolean.valueOf(st_SendMessage);
        boolean isEnable = Boolean.valueOf(st_Enable);
        sw_Terminate.setChecked(isTer);
        sw_SendMessage.setChecked(isSendMe);
        sw_Enable.setChecked(isEnable);
    }
    //set listener forQuicksetButton
    private void setListenerForQuickset()
    {
        btn15Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  int min = 15;
                setDisturb(min);
            }
        });



        btn30Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,"");
                int min = 30;
                setDisturb(min);
            }
        });



        btn1Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,"");
                int min = 60;
                setDisturb(min);
            }
        });




        btn2Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,"");
                int min = 120;
                setDisturb(min);
            }
        });




        btn4Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,"");
                int min = 240;
                setDisturb(min);
            }
        });



        btn8Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,"");
                int min = 480;
                setDisturb(min);
            }
        });



        btn12Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,"");
                int min = 720;
                setDisturb(min);
            }
        });


        btn24Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,"");
                int min = 1140;
                setDisturb(min);
            }
        });



        btn48Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,"");
                int min = 2880;
                setDisturb(min);
            }
        });

    }

    //setListener for switch
    private void setListenerForSw()
    {
        sw_Terminate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    myDatabaseHelper.updateSetting(SystemKey.IS_TERMINATE,String.valueOf(isChecked));
            }
        });

        sw_Enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myDatabaseHelper.updateSetting(SystemKey.IS_ENABLE,String.valueOf(isChecked));
                if(isChecked == true)
                {
                    setRingerMode(MainActivity.this,AudioManager.RINGER_MODE_SILENT);
                }
                else
                {
                    if(PREVIOUS_STATE.equals("true"))
                    {
                        setRingerMode(MainActivity.this,AudioManager.RINGER_MODE_SILENT);
                    }
                }
            }
        });

        sw_SendMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myDatabaseHelper.updateSetting(SystemKey.IS_SEND_MESSAGE,String.valueOf(isChecked));
            }
        });
    }




    private void setDisturb(int min)
    {
        Toast.makeText(MainActivity.this, "You set do no disturb from now to "+dateTimeHelper.addTime(min), Toast.LENGTH_LONG).show();
        myDatabaseHelper.updateSetting(SystemKey.IS_ENABLE,"true");
        getSetting();
        setCheck();
        myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,dateTimeHelper.addTimeToMilis(min));
    }

    private void setDisturb(String time)
    {
        Toast.makeText(MainActivity.this, "You set do no disturb from now to "+time, Toast.LENGTH_LONG).show();
        myDatabaseHelper.updateSetting(SystemKey.IS_ENABLE,"true");
        getSetting();
        setCheck();
        myDatabaseHelper.updateSetting(SystemKey.TIME_DISTURB,String.valueOf(dateTimeHelper.convertToDateTime(time).getMillis()));
    }

    public static boolean checkRingerIsOn(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    public void MappingMessage()
    {
        btnEdit = (Button) findViewById(R.id.btnEdit);
        message = (TextView) findViewById(R.id.txtMess1);
        getMess();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });


    }


    private void getMess()
    {
        String mess = myDatabaseHelper.getSettingValue(SystemKey.MESSAGE_TEMPLATE);
        message.setText(mess);
    }

    protected void showInputDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String mess = editText.getText().toString();
                       Toast.makeText(MainActivity.this, "Your message was changed",Toast.LENGTH_SHORT).show();
                       myDatabaseHelper.updateSetting(SystemKey.MESSAGE_TEMPLATE,mess);
                       getMess();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**********************************/
    //CHECK PERMISSION//
    /*********************************/
    int MY_PERMISSIONS_REQUEST = 1000;
    private void checkPermission() {
        String[] listPermission = new String[] {android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.READ_PHONE_STATE};
        boolean isOn = false;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            isOn = true;
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
        } else {
            isOn = true;
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            isOn = true;
        }
        if (isOn){
            ActivityCompat.requestPermissions(this, listPermission, MY_PERMISSIONS_REQUEST);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                switch (i) {
                    case 0:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        } else {

                        }
                        break;
                    case 1:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        } else {

                        }
                        break;
                    case 2:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        } else {

                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }



}
