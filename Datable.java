package sporting_event_ticketmanager.src;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Datable
 *
 * An interface for all classes which use the Gregorian Calendar
 * date, outlining the functions used in such classes.
 *
 *
 * @version November 6th, 2025
 *
 */
public interface Datable {
    /**
     * @return Gregorian Calender date
     */
    GregorianCalendar getDate();

    /**
     * @param date The date in which something occurs
     */
    void setDate(GregorianCalendar date);

    /**
     * @return Numerical day of the month
     */
    default int getDay() {
        return getDate().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return Numerical month of the year
     */
    default int getMonth() {
        return getDate().get(Calendar.MONTH);
    }

    /**
     * @return Numerical year
     */
    default int getYear() {
        return getDate().get(Calendar.YEAR);
    }
}
