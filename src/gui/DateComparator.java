package gui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class DateComparator implements Comparator<String> {
    private static final SimpleDateFormat DATE_FORMAT = 
        new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Override
    public int compare(String date1Str, String date2Str) {
        if (date1Str == null && date2Str == null) return 0;
        if (date1Str == null) return 1;
        if (date2Str == null) return -1;

        try {
            Date date1 = DATE_FORMAT.parse(date1Str);
            Date date2 = DATE_FORMAT.parse(date2Str);
            return date1.compareTo(date2);
        } catch (ParseException e) {
            // Fallback to string comparison if parsing fails
            return date1Str.compareTo(date2Str);
        }
    }
}

