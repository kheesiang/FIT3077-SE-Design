/**
 * Written by Bee Khee Siang & Aqeel Ahlam Rauf
 * */

package com.example.cholesterol.ServerCalls;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public interface APIListener {
    void onError(String message);

    void onResponse(JSONObject response, int counter) throws JSONException, ParseException, InterruptedException;
}
