package main.utils;

import main.constants.DateFormats;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final int MILLISECONDS_PER_SECOND = 1000;

    public Date addMS(Date date, Integer ms)
    {
        return add(Calendar.MILLISECOND, date, ms);
    }

    public Date removeMS(Date date, Integer ms)
    {
        return add(Calendar.MILLISECOND, date, -ms);
    }

    public Date addDays(Date date, int days)
    {
        return add(Calendar.DATE, date, days);
    }

    public Date addMonth(Date date, int month)
    {
        return add(Calendar.MONTH, date, month);
    }

    public Date add(int field, Date date, int diff) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(field, diff);
        return cal.getTime();
    }

    public boolean compareByDateOnly(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormats.COMPACT_DATE);
        return !(date1 == null || date2 == null) && sdf.format(date1).equals(sdf.format(date2));
    }

    public Date fromIsoFormat(String isoDTstring) {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat(DateFormats.ISO_DATETIME_WITH_TIMEZONE);
        try {
            return isoDateFormat.parse(isoDTstring);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toyyyyMMdd(Date date){
        DateFormat dateFormat = new SimpleDateFormat(DateFormats.SLASHED_DATE);
        return dateFormat.format(date);
    }

    public Date fromyyyyMMdd(String date){
        SimpleDateFormat isoDateFormat = new SimpleDateFormat(DateFormats.SLASHED_DATE);
        try {
            return isoDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long toUnixTime(Date date) {
        return date != null ? date.getTime() / MILLISECONDS_PER_SECOND : null;
    }
}
