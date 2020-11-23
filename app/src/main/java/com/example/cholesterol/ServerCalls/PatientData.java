package com.example.cholesterol.ServerCalls;

import android.content.Context;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cholesterol.Objects.Patient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PatientData {


    /**
     * This Function is used to send a request to the server
     * Request Type: Obtain Cholesterol Level of Patients
     * @param patients - This holds the HashMap of the Patients
     * @param context - Context
     */
    public static void getDetailedPatient(final HashMap<String, Patient> patients, final String patientID, final Context context) {

        RequestQueue queue = VolleyHandler.getInstance(context).getQueue();

         String url = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Patient/" + patientID + "?_format=json";


            try {

                JsonObjectRequest jsonObjectRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
//                                          Here we send the JSON response to be Parsed
                                            cleanDetails(response, patients, patientID);
                                        } catch (JSONException e) {
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


    /**
     * This class is used to clean and Parse the JSON response to obtain the value we need
     * @param response JSON object with the response from the server
     * @param patientHashMap The Hashmap with patient details existing
     * @param patientID The patient ID
     * @throws JSONException
     */
    public static void cleanDetails(JSONObject response, HashMap<String, Patient> patientHashMap, String patientID) throws JSONException {


//      We Parse the JSON to get the required values here:
        String gender = response.getString("gender");
        String birthDate = response.getString("birthDate");

        JSONObject address = response.getJSONArray("address").getJSONObject(0);
        String addressLine = address.getJSONArray("line").getString(0);
        String city = address.getString("city");
        String state = address.getString("state");
        String postalCode = address.getString("postalCode");
        String country = address.getString("country");

//      This shows you patient details in the console
        Log.d("gender", gender);
        Log.d("birthDate", birthDate);
        Log.d("addressLine", addressLine);
        Log.d("city", city);
        Log.d("state", state);
        Log.d("postalCode", postalCode);
        Log.d("country", country);

//      Here we set the details to the HashMap:
        patientHashMap.get(patientID).setExtraDetails(birthDate, gender, addressLine, city, postalCode, state, country);
    }
}
