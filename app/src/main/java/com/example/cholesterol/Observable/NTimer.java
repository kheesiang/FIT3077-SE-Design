/**
 * Written by Bee Khee Siang & Aqeel Ahlam Rauf
 * */

package com.example.cholesterol.Observable;

import android.util.Log;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;


public class NTimer extends Observable {


    public static int n = 0;

    public static int N = 100;

    /**
     * This Function is used to set the value of N
     * @param value - The value to set
     *
     */
    public static void setN(int value) {
        N = value;
    }

    /**
     * This Function is used to reset the value of n to 0(the counter)
     *
     */
    public static void resetN() {
        n = 0;
    }

    public static Timer timer = new Timer();

    public TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (n < N) {
                n++;
                Log.d("timer", String.valueOf(n));
            } else {
                task.cancel();
                timer.purge();
                Log.d("timer", "stopped");
                setChanged();
                notifyObservers();

            }
        }
    };

    /**
     * This Function is used to start the time and run it at a fixed rate
     *
     */
    public void startTimer() {
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

}
