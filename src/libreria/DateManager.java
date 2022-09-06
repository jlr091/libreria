/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package libreria;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Jose
 */
public class DateManager {

    public boolean isDateValid(String date) {
        String DATE_FORMAT = "dd-MM-yyyy";
        /**
         * **0123456789*
         */
        String DATE_FORMAT2 = "yyyy-MM-dd";
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            try {
                DateFormat df = new SimpleDateFormat(DATE_FORMAT2);
                df.setLenient(false);
                df.parse(date);
                return true;
            } catch (ParseException a) {
                return false;
            }
        }
    }

    public String DateToDB(String oldDateString) {
        final String OLD_FORMAT = "dd-MM-yyyy";
        /**
         * **0123456789
         */
        final String NEW_FORMAT = "yyyy-MM-dd";
        String newDateString = oldDateString;
        if (!oldDateString.substring(6, 10).contains("-")) {
            newDateString = oldDateString.substring(6, 10) + "-"
                    + oldDateString.substring(3, 5) + "-"
                    + oldDateString.substring(0, 2);
        }
        return newDateString;
    }

    public Date DateFormatDate(String oldDateString) throws ParseException {
        final String OLD_FORMAT = "dd-MM-yyyy";
        /**
         * **0123456789
         */
        final String NEW_FORMAT = "yyyy-MM-dd";
        String newDateString = oldDateString;
        if (!oldDateString.substring(6, 10).contains("-")) {
            newDateString = oldDateString.substring(6, 10) + "-"
                    + oldDateString.substring(3, 5) + "-"
                    + oldDateString.substring(0, 2);
        }
        Date dt = new SimpleDateFormat("yyyy-MM-dd").parse(newDateString);
        return dt;
    }

    public String DateToFront(String oldDateString) throws ParseException {
        final String OLD_FORMAT = "yyyy-MM-dd";
        /**
         * **0123456789
         */
        final String NEW_FORMAT = "dd-MM-yyyy";
        String newDateString = oldDateString;
        if (!oldDateString.substring(0, 4).contains("-")) {
            newDateString = oldDateString.substring(8, 10) + "-"
                    + oldDateString.substring(5, 7) + "-"
                    + oldDateString.substring(0, 4);
        }
        return newDateString;
    }

    public String StringToDate(String oldDateString) throws ParseException {
        final String OLD_FORMAT = "yyyy-MM-dd";
        /**
         * **0123456789
         */
        final String NEW_FORMAT = "dd-MM-yyyy";
        String newDateString = oldDateString;
        if (!oldDateString.substring(0, 4).contains("-")) {
            newDateString = oldDateString.substring(8, 10) + "-"
                    + oldDateString.substring(5, 7) + "-"
                    + oldDateString.substring(0, 4);
        } else {
            newDateString = oldDateString.substring(6, 10) + "-"
                    + oldDateString.substring(3, 5) + "-"
                    + oldDateString.substring(0, 2);
        }
        return newDateString;
    }

    public boolean isValidRange(String date_1, String date_2) throws ParseException {
        DateManager a = new DateManager();
        Date historyDate = a.DateFormatDate(date_1);
        Date futureDate = a.DateFormatDate(date_2);

        if (futureDate.after(historyDate) || historyDate.equals(futureDate)) {
            return TRUE;

        } else {
            return FALSE;
        }
    }

    public String lastDayMonth(String date, Boolean Quincena) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        DateManager dateManager = new DateManager();

        calendar.setTime(dateManager.DateFormatDate(date));
        calendar.add(Calendar.MONTH, -1);

        // Date currentDate = (Date) calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha_al = dateFormat.format(calendar.getTime());

        if (Quincena) {
            fecha_al = fecha_al.substring(0, 8).concat("15");
        } else {
            fecha_al = fecha_al.substring(0, 8).concat(Integer.toString(calendar.getActualMaximum(Calendar.DATE)));
        }

        return dateManager.DateToFront(fecha_al);
    }
}
