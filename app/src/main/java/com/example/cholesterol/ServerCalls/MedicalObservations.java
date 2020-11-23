/**
 * Written by Bee Khee Siang & Aqeel Ahlam Rauf
 * */

package com.example.cholesterol.ServerCalls;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cholesterol.Objects.Patient;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.HashMap;

public abstract class MedicalObservations {

    MedicalObservations() {
    }

    public abstract void cleanObservation(String job, int totalObservationTypes, boolean graphView, JSONObject response, HashMap<String, Patient> monitoredPatients, String patientID, final RecyclerView recyclerView, final Context context, int counter, int max_length) throws JSONException, ParseException;

    public abstract void cleanLatestXObservations(String job, int totalObservationTypes, boolean graphView, int X, JSONObject response, HashMap<String, Patient> monitoredPatients, String patientID, final RecyclerView recyclerView, final Context context, int counter, int max_length) throws JSONException, ParseException;

}

