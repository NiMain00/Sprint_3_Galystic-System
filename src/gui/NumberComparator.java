package gui;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Locale;
import java.lang.Number;

public class NumberComparator implements Comparator<Object> {
    private static final DecimalFormat DF = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());

    static {
        DF.setParseBigDecimal(false);
    }

    private double getNumericValue(Object obj) {
        if (obj == null) return 0;
        
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        
        String str = obj.toString().replace("Rp ", "").replace(",", "").trim();
        if (str.isEmpty()) return 0;
        
        try {
            return DF.parse(str).doubleValue();
        } catch (ParseException | NumberFormatException e) {
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException e2) {
                return 0;
            }
        }
    }

    @Override
    public int compare(Object o1, Object o2) {
        double num1 = getNumericValue(o1);
        double num2 = getNumericValue(o2);
        return Double.compare(num1, num2);
    }
}
