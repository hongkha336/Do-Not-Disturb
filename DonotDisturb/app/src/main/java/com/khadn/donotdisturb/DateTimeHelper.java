package com.khadn.donotdisturb;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeHelper {

    public String addTime(int min)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        DateTime localDateTime = new DateTime();
        localDateTime = localDateTime.plusMinutes(min);
        return String.valueOf(formatter.print(localDateTime));
    }


    public String getDate()
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
        DateTime localDateTime = new DateTime();
        return String.valueOf(formatter.print(localDateTime));
    }

    public String getDateTime()
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        DateTime localDateTime = new DateTime();
        return String.valueOf(formatter.print(localDateTime));
    }


    public String addTimeToMilis(int min)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        DateTime localDateTime = new DateTime();
        localDateTime = localDateTime.plusMinutes(min);
        return String.valueOf(localDateTime.getMillis());
    }

    public DateTime convertToDateTime(String a)
    {
        //must be like this 04/02/2011 20:27:05
        DateTime dt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(a);
        return  dt;
    }

    public int compareDateTime(String DT1, String DT2)
    {
        //if dt1 > dt2 => 1
        // dt1 < dt2  => -1
        //dt1 = dt2 => 0
        if(Long.valueOf(DT1) > Long.valueOf(DT2))
            return 1;
        if(Long.valueOf(DT1) > Long.valueOf(DT2))
            return -1;
        if(Long.valueOf(DT1) > Long.valueOf(DT2))
            return 0;
        return 2;

    }
}
