package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class Utils
{
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isBoolean(String s)
    {
       s = s.toLowerCase();

       if(s.equals("true"))
       {
           return true;
       }

       if(s.equals("false"))
       {
           return false;
       }

       return false;
    }

    public static boolean isDate(String s)
    {
        DateFormat df = new SimpleDateFormat("YYYY-MM-DD");

        try
        {
            df.parse(s);
            return true;
        }catch (ParseException e)
        {
            return false;
        }
    }

    public static boolean isPhoneNumber(String s)
    {
        String regex = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";

        return s.matches(regex);
    }
}
