package com.example.cholesterol.ServerCalls;

import android.content.Context;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cholesterol.Adapters.BPMonitorAdapter;
import com.example.cholesterol.Adapters.MonitorAdapter;
import com.example.cholesterol.Objects.Patient;
import com.example.cholesterol.UserInterfaces.BPMonitorActivity;
import com.example.cholesterol.UserInterfaces.MainActivity;
import com.example.cholesterol.UserInterfaces.MonitorActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BloodPressureData extends MedicalObservations {

    public BloodPressureData() {
        super();
    }


    @Override
    public void cleanObservation(final String job, final int totalObservationTypes, boolean graphView, JSONObject response, HashMap<String, Patient> monitoredPatients, String patientID, RecyclerView recyclerView, Context context, int counter, int max_length) throws JSONException, ParseException {
        int total = response.getInt("total");
        if (total > 0) {

            JSONArray entry = response.getJSONArray("entry");
            double systolicBP = entry.getJSONObject(0).getJSONObject("resource").getJSONArray("component").getJSONObject(1).getJSONObject("valueQuantity").getInt("value");
            String systolicBPUnit = entry.getJSONObject(0).getJSONObject("resource").getJSONArray("component").getJSONObject(1).getJSONObject("valueQuantity").getString("unit");
            systolicBPUnit = systolicBPUnit.replaceAll("[^A-Za-z]", "");

            double diasystolicBP = entry.getJSONObject(0).getJSONObject("resource").getJSONArray("component").getJSONObject(0).getJSONObject("valueQuantity").getInt("value");
            String diasystolicBPUnit = entry.getJSONObject(0).getJSONObject("resource").getJSONArray("component").getJSONObject(0).getJSONObject("valueQuantity").getString("unit");
            diasystolicBPUnit = diasystolicBPUnit.replaceAll("[^A-Za-z]", "");

            String effectiveDate = entry.getJSONObject(0).getJSONObject("resource").getString("effectiveDateTime");

            String result;

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
            Date d = df.parse(effectiveDate);
            df.applyPattern("dd-M-yyyy hh:mm:ss");
            result = df.format(d);


//            if (job.equals("Update")) {
//                //FOR DEMONSTRATION PURPOSES
//                systolicBP = Math.random() * 100;
//                systolicBP = Math.round(systolicBP * 00d);
//
//                diasystolicBP = Math.random() * 100;
//                diasystolicBP = Math.round(diasystolicBP * 100d);
//                Log.d("updateBP", String.valueOf(systolicBP));
//
//            }

            monitoredPatients.get(patientID).setSystolic(systolicBP + systolicBPUnit);
            monitoredPatients.get(patientID).setDiastolic(diasystolicBP + diasystolicBPUnit);
            monitoredPatients.get(patientID).setEffectiveDateBP(result);
        }

//        Log.d("job", job);
        if (job.equals("Update") && counter == max_length) {
            MonitorAdapter monitorAdapter = new MonitorAdapter(MainActivity.getMonitoredPatients(), MainActivity.context);
            MonitorActivity.refresh(monitorAdapter);
        }
    }

    @Override
    public void cleanLatestXObservations(String job, int totalObservationTypes, boolean graphView, int X, JSONObject response, HashMap<String, Patient> monitoredPatients, String patientID, RecyclerView recyclerView, Context context, int counter, int max_length) throws JSONException, ParseException {
        JSONArray entry = response.getJSONArray("entry");

        String systolicBPUnit = entry.getJSONObject(0).getJSONObject("resource").getJSONArray("component").getJSONObject(1).getJSONObject("valueQuantity").getString("unit");
        systolicBPUnit = systolicBPUnit.replaceAll("[^A-Za-z]","");

        double systolicBP = 0;
        String effectiveDate;
        String result = "";
        for (int i=0; i < X; i++) {
            systolicBP = entry.getJSONObject(i).getJSONObject("resource").getJSONArray("component").getJSONObject(1).getJSONObject("valueQuantity").getInt("value");
            effectiveDate = entry.getJSONObject(i).getJSONObject("resource").getString("effectiveDateTime");

//            if (job.equals("Update")) {
//                systolicBP = Math.random()*100;
//            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
            Date d = df.parse(effectiveDate);
            df.applyPattern("dd-M-yyyy hh:mm:ss");
            result = df.format(d);

            monitoredPatients.get(patientID).setXLatestBP(i, result, systolicBP + systolicBPUnit);
        }

        Log.d("XBP", job);

        if (job.equals("Update")) {
            if (! graphView){
                if (counter == max_length) {
                    BPMonitorAdapter bpMonitorAdapter = new BPMonitorAdapter(MonitorAdapter.getHighSystolic(), BPMonitorActivity.context);
                    BPMonitorActivity.refresh(bpMonitorAdapter);
                }
            }
        }
    }


}
