package com.bhcc.app.pharmtech.data.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This class class manages the "Drug of The Day" feature.
 * This class has a shuffled list of the drug generic names, an index for
 * which one was shown last, and a time of when a new drug of the day can be shown.
 *
 * @author Nils Johnson
 */

public class DrugOfDayManager implements Serializable
{
    private static final String TAG = "DrugOfDayManager";
    private Date nextNewDrugTime;
    private int drugOfDayIndex;
    private ArrayList<String> drugOfDayList;
    private boolean drugShownToday = false;

    /**
     * This object is to only be made once, and then loaded from a file each time it is needed to be
     * accessed.
     *
     * @param medicines a list of medicines which can be drug of the day
     */
    public DrugOfDayManager(List<Medicine> medicines)
    {
        setNextDay();
        // makes an array list with space reserved for each element
        drugOfDayList = new ArrayList<>(medicines.size() + 1);
        drugOfDayIndex = 0;

        for (int i = 0; i < medicines.size(); i++)
        {
            drugOfDayList.add(medicines.get(i).getGenericName());
        }

        Collections.shuffle(drugOfDayList);
    }

    /**
     * Sets a point in time that is midnight of the next day, which is when the program knows will
     * show another drug of the day.
     */
    private void setNextDay()
    {
        // today
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
        date.add(Calendar.DAY_OF_MONTH, 1);

        nextNewDrugTime = new Date(date.getTime().toString());

        Log.d(TAG, "Next new drug of day: " + nextNewDrugTime.toString());
    }

    /**
     * @return the drug of the day and sets a boolean to remember that the user has already seen
     * the drug of the day on this day.
     */
    public String getDrugOfDay()
    {
        drugShownToday = true;
        if (new Date().getTime() < nextNewDrugTime.getTime())
        {
            return drugOfDayList.get(drugOfDayIndex);
        } else
        {
            setNextDay();
            drugOfDayIndex = (drugOfDayIndex + 1) % drugOfDayList.size();
            return drugOfDayList.get(drugOfDayIndex);
        }
    }

    /**
     * @return a boolean, stating whether of not a drug has been shown yet on this day.
     */
    public boolean drugShownToday()
    {
        return drugShownToday;
    }
}
