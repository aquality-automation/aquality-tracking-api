package main.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public Date addMS(Date date, Integer ms)
    {
        return add(Calendar.MILLISECOND, date, ms);
    }

    public Date addDays(Date date, int days)
    {
        return add(Calendar.DATE, date, days);
    }

    public Date addMonth(Date date, int month)
    {
        return add(Calendar.MONTH, date, month);
    }

    public Date add(int field, Date date, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, month);
        return cal.getTime();
    }

    public boolean compareByDateOnly(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return !(date1 == null || date2 == null) && sdf.format(date1).equals(sdf.format(date2));
    }

    public Date fromIsoFormat(String isoDTstring) {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        try {
            return isoDateFormat.parse(isoDTstring);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toyyyyMMdd(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date);
    }

    public Date fromyyyyMMdd(String date){
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            return isoDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
