package org.example;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String PATH = "src/main/resources/";

    public static boolean isValidTime(String time) {

        // Regex to check valid time in 24-hour format.
        String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the time is empty
        // return false
        if (time == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given time
        // and regular expression.
        Matcher m = p.matcher(time);

        // Return if the time
        // matched the ReGex
        return m.matches();
    }


    public static Timestamp timestampCoverter(String time) throws java.text.ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date parsedDate = dateFormat.parse(time);

        return new java.sql.Timestamp(parsedDate.getTime());
    }

    public static Timestamp addOneDay(Timestamp closeTime) {
        return Timestamp.valueOf(closeTime.toLocalDateTime().plusDays(1));
    }

}
