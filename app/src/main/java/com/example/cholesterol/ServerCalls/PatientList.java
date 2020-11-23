package com.example.cholesterol.ServerCalls;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cholesterol.Adapters.PatientListAdapter;
import com.example.cholesterol.UserInterfaces.MainActivity;
import com.example.cholesterol.Objects.Patient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientList {

    private static ArrayList<JSONObject> jsonData = new ArrayList<>();

    /**
     * This Function is used to add the API response to the jsonData
     * @param response - the response
     *
     */
    public static void addJsonData(JSONObject response) {
        if (response != null) {
            jsonData.add(response);
        }
    }

    /**
     * This Function is used to set jsonData
     * @param response - the response to set
     *
     */
    public static void setJsonData(ArrayList<JSONObject> response) {
        jsonData = response;
    }

    /**
     * This Function is used to get jsonData
     *
     */
    public static ArrayList<JSONObject> getJsonData() {
        return jsonData;
    }


    /**
     * This Function handles the API calls needed to get the HP identifier, then to get all his patients from all the API pages
     * @param practitionerID - the practitioner id
     * @param context - Context
     * @param recyclerView - the recyler View
     * @param patientListHash - This holds the HashMap of the patients
     * @param monitoredPatients - This holds the HashMap of the monitored patients
     *
     */
    public static void patientHandler(String practitionerID, final Context context, final RecyclerView recyclerView, final HashMap<String, Patient> patientListHash, final HashMap<String, Patient> monitoredPatients) {
        ArrayList<JSONObject> reset = new ArrayList<>();
        setJsonData(reset);

        getPatientList(practitionerID, context, recyclerView, new APIListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onResponse(JSONObject response, int temp) throws JSONException {

                addJsonData(response);

                JSONArray link = response.getJSONArray("link");

                int counter = 100;
                final int max = 700;


                String url = null;
                for (int i = 0; i < link.length(); i++) {
                    if (link.getJSONObject(i).getString("relation").equals("next"))   {
                        url = link.getJSONObject(i).getString("url");
                    }
                }

                String[] tempArray = url.split("_getpagesoffset=");
                url = tempArray[0] + "_getpagesoffset=";
                String filters = "&_count=100&_format=json&_pretty=true&_bundletype=searchset";


                while (counter < max) {
                    String new_url = url + counter + filters;

                    Log.d("counter", String.valueOf(counter));

                    counter += 100;
                    Log.d("url", new_url);
                    recursiveHandler(new_url, context, counter, new APIListener() {
                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onResponse(JSONObject response, int counter2) throws JSONException {
                            addJsonData(response);
                            ArrayList<JSONObject> results = getJsonData();
                            Log.d("currentResultsSize", String.valueOf(results.size()));

                            if (counter2 == max) {
                                cleanPatientList(getJsonData(), context, recyclerView, patientListHash, monitoredPatients);
                            }
                        }
                    });
//                        }
//                    }
                }
            }
        });
    }


    /**
     * This Function handles the recursive API calls that iterates the pages of the Encounter API
     * @param url - the url to call
     * @param context - Context
     * @param counter - the counter to keep track of the pages called
     * @param listener - the APIListener to handle the callback
     *
     */
    private static void recursiveHandler(String url, final Context context, final int counter, final APIListener listener) throws JSONException {
        RequestQueue queue = VolleyHandler.getInstance(context).getQueue();

        try {
            JsonObjectRequest stringRequest =
                    new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        listener.onResponse(response, counter);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("recursive", error.getMessage());
                                    ArrayList<JSONObject> results = getJsonData();
                                    Log.d("currentResults", String.valueOf(results.size()));
                                }
                            });


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    100000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.start();
            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * This Function gets the practitioners identifier
     * @param practitionerID - the practitioner id
     * @param context - Context
     * @param recyclerView - the recycler View
     * @param listener - the APIListener to handle the callback
     *
     */
    public static void getPatientList(String practitionerID, final Context context, final RecyclerView recyclerView, final APIListener listener) {
        RequestQueue queue = VolleyHandler.getInstance(context).getQueue();

        String url = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Practitioner/" + practitionerID + "?_format=json";

        try {
            if (practitionerID.isEmpty()) {
                Toast.makeText(MainActivity.context, "Cannot be left empty", Toast.LENGTH_LONG).show();
            } else {

                JsonObjectRequest stringRequest =
                        new JsonObjectRequest(Request.Method.GET, url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            aux_getPatientList(response, context, recyclerView, listener);
                                        } catch (Exception e) {
                                        }
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("stock", error.getMessage());
                                        listener.onError(error.toString());
                                    }
                                });

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        100000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.start();
                queue.add(stringRequest);
            }

        } catch (Exception e) {
        }

    }


    /**
     * This Function gets the first page of encounter api of the practitioner
     * @param response - the API response
     * @param context - Context
     * @param recyclerView - the recycler View
     * @param listener - the APIListener to handle the callback
     *
     */
    public static void aux_getPatientList(JSONObject response, final Context context, final RecyclerView recyclerView, final APIListener listener) {
        RequestQueue queue = VolleyHandler.getInstance(context).getQueue();

        String identifier = null;
        if (response != null) {
            try {
                String identifier1 = "";
                String identifier2 = "";
                JSONArray identify = response.getJSONArray("identifier");
                identifier1 = identify.getJSONObject(0).getString("system");
                identifier2 = identify.getJSONObject(0).getString("value");

                identifier = identifier1 + "%7C" + identifier2;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (identifier != null) {
            String firstUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/Encounter?_include=Encounter.participant.individual&_include=Encounter.patient&participant.identifier="
                    + identifier + "&_sort=-date&_count=100&_format=json";


            try {
                Log.d("firstUrl", firstUrl);

                JsonObjectRequest stringRequest =
                        new JsonObjectRequest(Request.Method.GET, firstUrl, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            listener.onResponse(response, 0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("aux", error.getMessage());
                                        listener.onError(error.toString());
                                    }
                                });


                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        100000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                queue.start();
                queue.add(stringRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * This Function cleans the responses from all the encounter api calls
     * @param response - list of responses
     * @param context - Context
     * @param recyclerView - the recycler View
     * @param patientListHash - This holds the HashMap of the patients
     * @param monitoredPatients - This holds the HashMap of the monitored patients
     *
     */
    public static void cleanPatientList(ArrayList<JSONObject> response, final Context context, final RecyclerView recyclerView, HashMap<String, Patient> patientListHash, HashMap<String, Patient> monitoredPatients) throws JSONException {

//      This Hashmap holds information regarding the patient where the key would be the patientID

        for (int i = 0; i < response.size(); i++) {
            try {
                String idString;
                String patientID;
                String name;
                JSONArray entry = response.get(i).getJSONArray("entry");
                for (int j = 0; j < entry.length(); j++) {
                    try {
                        idString = entry.getJSONObject(j).getJSONObject("resource").getJSONObject("subject").getString("reference");
                        name = (entry.getJSONObject(j).getJSONObject("resource").getJSONObject("subject").getString("display")).replaceAll("[\\d]", "");
                        patientID = idString.substring(8);

//                      Create a new Patient object to hold the information of the patient
                        Patient patient = new Patient(patientID, name);

//                      If the patient is already in the HashMap, we skip
                        if (!patientListHash.containsKey(patientID)){
                            patientListHash.put(patientID, patient);
                        }


                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }

//        CholesterolData.getCholesterol(patientListHash, monitoredPatients, context, recyclerView);
        PatientListAdapter patientListAdapter = new PatientListAdapter(patientListHash, monitoredPatients);
        recyclerView.setAdapter(patientListAdapter);

    }

}
