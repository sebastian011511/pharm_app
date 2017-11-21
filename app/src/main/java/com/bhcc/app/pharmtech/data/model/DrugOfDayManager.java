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
 * which one was shown last, and the time when the current drug of the day expires.
 *
 * @author Nils Johnson
 */

public class DrugOfDayManager implements Serializable
{
    private static final String TAG = "DrugOfDayManager";
    private Date expirationDate;
    private int index = 0;
    private ArrayList<String> drugOfDayList = new ArrayList<>();
    private ArrayList<Boolean> drugShown = new ArrayList<>();

    /**
     * This object is to only be made once, and then loaded from a file each time it is needed to be
     * accessed.
     *
     * @param medicines a list of medicines which can be drug of the day
     */
    public DrugOfDayManager(List<Medicine> medicines)
    {
        setExpirationDate();

        // initialize lists with drug names and false
        for (int i = 0; i < medicines.size(); i++)
        {
            drugOfDayList.add(medicines.get(i).getGenericName());
            drugShown.add(false);
        }

        // shuffle so that drug of the day is random
        Collections.shuffle(drugOfDayList);
    }

    /**
     * Sets a point in time that is midnight of the next day, which is when the program knows will
     * show another drug of the day.
     */
    private void setExpirationDate()
    {
        // today
        Calendar cal = new GregorianCalendar();

        // reset hour, minutes, seconds and millis
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // next day
        cal.add(Calendar.DAY_OF_MONTH, 1);

        // set expiration date to be next day
        expirationDate = cal.getTime();

        Log.d(TAG, "Current Drug Of The Day Will Expire: " + expirationDate.toString());
    }

    /**
     * @return the drug of the day and sets a boolean to remember that the user has already seen
     * the drug of the day on this day.
     */
    public String getDrugOfDay()
    {
        // if this instant is before the next expiration date, return the current drug of the day
        if (new Date().before(expirationDate))
        {
            drugShown.set(index, true);
            return drugOfDayList.get(index);
        }
        // else, the instant is after the expiration day, which means we need to set the expiration date,
        // increment the index to show the next drug
        else
        {
            // set the previous drug of the day to being not shown, so the program doesnt break if the user goes through all 200 drugs.
            drugShown.set(index, false);
            setExpirationDate();
            index = (index + 1) % drugOfDayList.size();
            drugShown.set(index, true);
            return drugOfDayList.get(index);
        }
    }

    public Date getExpirationDate()
    {
        return this.expirationDate;
    }

    public boolean shownToday()
    {
        Date rightNow = new Date();

        // if right now is before the expiration date, return whether or no that drug has been shown.
        if(rightNow.before(expirationDate))
        {
            return drugShown.get(index);
        }
        // else we are ready for a new drug of the day. return the status of the next one, which should be false.
        else
        {
            return drugShown.get(index + 1);
        }
    }
}
