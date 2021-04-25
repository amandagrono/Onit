package com.temple.onit.Alarms;

import android.util.Log;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;

import java.util.Arrays;
import java.util.Calendar;

public class SmartAlarmTest extends TestCase {

    SmartAlarm alarm= new SmartAlarm(
            132423,
            9,
            30,
            23,
            389,
            "Title",
            "0111110",
            true,
            true,
            39,
            -75,
            39,
            -75.05,
            System.currentTimeMillis());

    @Before
    public void init(){
    }

    public void testEnabledOnDay() {
        assertTrue(alarm.enabledOnDay(1));
        assertFalse(alarm.enabledOnDay(0));
    }

    public void testMillisToHours() {
        long millis = 33300000;
        assertEquals(9, alarm.millisToHours(millis));
    }

    public void testMillisToMinutes() {
        long millis = 33300000;
        assertEquals(15, alarm.millisToMinutes(millis));
    }
    public void testSetDaysArray(){
        int[] daysArray;
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
            case(1):
                daysArray = new int[]{0, 1, 2, 3, 4, 5, 6};
                break;
            case(2):
                daysArray = new int[]{1, 2, 3, 4, 5, 6, 0};
                break;
            case(3):
                daysArray = new int[]{2, 3, 4, 5, 6,0,1};
                break;
            case(4):
                daysArray = new int[]{3, 4, 5, 6, 0, 1, 2};
                break;
            case(5):
                daysArray = new int[]{ 4, 5, 6, 0, 1, 2, 3};
                break;
            case(6):
                daysArray = new int[]{ 5, 6, 0, 1, 2, 3, 4};
                break;
            case(7):
                daysArray = new int[]{6, 0, 1, 2, 3, 4, 5};
                break;
            default:
                // it will never get to this point because calendar.dayofweek only returns 1-7. It was just not letting me run the test because the array may not have been initialized
                daysArray = new int[]{0,1,2,3,4,5,6};
                break;
        }

        int[] testArray = SmartAlarm.setDayArray();

        Log.d("Test Array", Arrays.toString(testArray));
        Log.d("Days Array", Arrays.toString(daysArray));
        Assert.assertArrayEquals(daysArray, testArray);
    }
}