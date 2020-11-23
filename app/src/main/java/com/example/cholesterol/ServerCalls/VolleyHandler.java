package com.example.cholesterol.ServerCalls;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleyHandler {
    private static VolleyHandler instance;
    private RequestQueue queue;
    private static Context context;

    private VolleyHandler(Context context) {
        VolleyHandler.context = context;
        queue = getQueue();
    }

    /**
     * This Function gets the instance
     * @param context - Context
     */
    public static synchronized VolleyHandler getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyHandler(context);
        }
        return instance;
    }

    /**
     * This Function gets the volley queue
     *
     */
    public RequestQueue getQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return queue;
    }

    /**
     * This Function adds to the volley queue
     * @param x - the Request
     *
     */
    public  void addToQueue(Request x) {
        getQueue().add(x);
    }

}