package com.example.cholesterol.ServerCalls;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cholesterol.Objects.Patient;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.HashMap;

public class ObservationHandler {

    /**
     * This function is used to make the API calls, It is catered to run for both Cholesterol &
     * Blood Pressure Values
     * @param job This string indicates if we have to make the initial call to the API or Update
     *           the Relevant values
     * @param totalObservationTypes This is used when we have multiple observations
     * @param observationType This is String is used to pull either "Chol" or "BP" or "XBP"
     * @param graphView Used when populating the Graphs
     * @param monitoredPatients Hashmap of Monitored Patients
     * @param context Activity's Context
     * @param recyclerView The View used to populate
     */
    public static void getObservation(final String job, final int totalObservationTypes, final String observationType, final boolean graphView, final HashMap<String, Patient> monitoredPatients, final Context context, final RecyclerView recyclerView){
        RequestQueue queue = VolleyHandler.getInstance(context).getQueue();

        final Object[] patientsBundle = monitoredPatients.keySet().toArray();

        assert patientsBundle != null;
        for(int i = 0; i < patientsBundle.length; i++){

            String url = "";
            if (observationType.equals("Chol")) {
                url = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Observation?_count=13&code=2093-3&patient=" + patientsBundle[i] + "&_sort=-date&_format=json";
            }
            else if (observationType.equals("BP") || observationType.equals("XBP")) {
                url = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Observation?_count=13&code=55284-4&patient=" + patientsBundle[i] +  "&_sort=-date&_format=json";

            }

            final String patientID = patientsBundle[i].toString();
//            Log.d("PatientID", patientID);

            try {

                final int counter = i;
                final int max_length = patientsBundle.length - 1;
                JsonObjectRequest jsonObjectRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
//                                            Log.d("PeopleWithobservation", patientID);
                                            if (observationType.equals("Chol")){
                                                CholesterolData cholesterolData = new CholesterolData();
                                                cholesterolData.cleanObservation(job, totalObservationTypes, graphView, response, monitoredPatients, patientID ,recyclerView, context, counter, max_length);
                                            }
                                            else if (observationType.equals("BP")) {
                                                BloodPressureData bloodPressureData = new BloodPressureData();
                                                bloodPressureData.cleanObservation(job, totalObservationTypes, graphView, response, monitoredPatients, patientID ,recyclerView, context, counter, max_length);
                                            }
                                            else if (observationType.equals("XBP")) {
                                                BloodPressureData bloodPressureData = new BloodPressureData();
                                                bloodPressureData.cleanLatestXObservations(job, totalObservationTypes, graphView, 5, response, monitoredPatients, patientID ,recyclerView, context, counter, max_length);
                                            }

                                        } catch (JSONException | ParseException e) {
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        100000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.start();
                queue.add(jsonObjectRequest);

            } catch (Exception e) {
            }
        }
    }


}
