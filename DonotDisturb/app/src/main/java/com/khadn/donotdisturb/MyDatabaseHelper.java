package com.khadn.donotdisturb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SETTINGMANAGER";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "SETTING";

    private static final String KEY_ID = "_KEY";
    private static final String KEY_VALUE = "_VALUE";


    public MyDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_customer_table = String.format("CREATE TABLE %s(%s TEXT PRIMARY KEY, %s TEXT)", TABLE_NAME, KEY_ID, KEY_VALUE);
        db.execSQL(create_customer_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_customer_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_customer_table);
        onCreate(db);
    }


    public void addSetting(Setting setting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, setting.get_KEY());
        values.put(KEY_VALUE, setting.get_VALUE());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String getSettingValue(String KEY) {
        List<Setting> List = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {
            Setting setting = new Setting(cursor.getString(0), cursor.getString(1));
            List.add(setting);
            cursor.moveToNext();
        }

        for(Setting st : List)
        {
            if(st.get_KEY().equals(KEY))
                return  st.get_VALUE();
        }

        return "error";
    }

    // create if not exist the basic setting
    public void setDefaulSettingIfNeed()
    {
        Setting s0 = new Setting(SystemKey.IS_TERMINATE,"true");
        Setting s1 = new Setting(SystemKey.TIME_DISTURB,"");
        Setting s2 = new Setting(SystemKey.IS_ENABLE,"false");
        Setting s3 = new Setting(SystemKey.IS_SEND_MESSAGE, "true");
        Setting s4 = new Setting(SystemKey.MESSAGE_TEMPLATE,"Do not distrub application have just terminate your phone call");
        Setting s5 = new Setting(SystemKey.PRIVIOUS_STATE, "");
        try {
            addSetting(s1);
            addSetting(s2);
            addSetting(s3);
            addSetting(s0);
            addSetting(s4);
            addSetting(s5);
        }
        catch (Exception e){}
    }


    public void updateSetting(String key, String value)
    {
        SQLiteDatabase    database = this.getReadableDatabase();
        ContentValues args = new ContentValues();
        args.put(KEY_VALUE, value);
        database.update(TABLE_NAME, args, KEY_ID + " = ?", new String[]{key});
        database.close();
    }

}