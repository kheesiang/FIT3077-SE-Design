/**
 * Written by Bee Khee Siang & Aqeel Ahlam Rauf
 * */

package com.example.cholesterol.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cholesterol.ServerCalls.ObservationHandler;
import com.example.cholesterol.UserInterfaces.MainActivity;
import com.example.cholesterol.Objects.Patient;
import com.example.cholesterol.R;
import com.example.cholesterol.UserInterfaces.MonitorActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


public class MonitorAdapter extends RecyclerView.Adapter<MonitorAdapter.MonitorListView> implements Observer {

    private HashMap<String, Patient> monitoredPatientListHash;
    private Context context;
    private static HashMap<String, Patient> highSystolicHash = new HashMap<>();

    /**
     * Accessors & Mutator for Variables that will be used/needed by other classes
     */
    public static HashMap<String, Patient> getHighSystolic() {
        return highSystolicHash;
    }

    /**
     * Constructor for MonitorAdapter
     * @param monitoredHash HashMap of Patients
     * @param context Context
     */
    public MonitorAdapter(HashMap<String, Patient> monitoredHash, Context context) {
        this.monitoredPatientListHash = monitoredHash;
        this.context = context;
    }

    /**
     * This method will be called whenever a ViewHolder is created(An Instance of ViewHolder class below)
     * @param parent Parent object of the Layout
     */
    @NonNull
    @Override
    public MonitorListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View monitorPatient = inflater.inflate(R.layout.monitor_patients, parent, false);
        MonitorListView view = new MonitorListView(monitorPatient);

        return view;
    }

    /**
     * This method binds data to view holder (Each Card in the recycler view)
     * @param holder UI Element
     * @param position Current Position of the card
     */
    @Override
    public void onBindViewHolder(@NonNull final MonitorListView holder, final int position) {
        final Object[] keys = monitoredPatientListHash.keySet().toArray();

        final String patientID = monitoredPatientListHash.get(keys[position]).getPatientID();
        final String patientname = monitoredPatientListHash.get(keys[position]).getName();

//      This is to get the Cholesterol Levels from the list of monitored patients
        final String chol = monitoredPatientListHash.get(keys[position]).getCholesterol();
        final String effectiveDateChol = monitoredPatientListHash.get(keys[position]).getEffectiveDateChol();

//      This is to get the Blood Pressure values from the list of monitored patients
        final String systolicHash = monitoredPatientListHash.get(keys[position]).getSystolic();
        final String diastolicHash = monitoredPatientListHash.get(keys[position]).getDiastolic();
        final String effectiveDateBP = monitoredPatientListHash.get(keys[position]).getEffectiveDateBP();

//      This is to perform a comparison and set the color for high levels of Blood Pressure
        double systolicHashParse = Double.parseDouble(systolicHash.replaceAll("[^\\d\\.]", ""));
        double diastolicHashParse = Double.parseDouble(diastolicHash.replaceAll("[^\\d\\.]", ""));

//      This will set the Patient Name
        holder.PatientTV.setText(patientname);

//      This is to perform a comparison and set the color for high levels of Cholesterol
        double AverageCholesterol = getAverageReadings(monitoredPatientListHash);
        String numericChol = chol.replaceAll("[^\\d\\.]", "");
        double finalChol = Double.parseDouble(numericChol);

        holder.CholEffectiveDateTV.setText(effectiveDateChol);
        holder.BPEffectiveDateTV.setText(effectiveDateBP);


//      This is where we hide the view if not required by practitioner
        if(!MonitorActivity.isCholSwitch()){
            holder.CholLevelTV.setVisibility(View.GONE);
            holder.CholEffectiveDateTV.setVisibility(View.GONE);
            holder.CholTV1.setVisibility(View.GONE);
            holder.CholTV2.setVisibility(View.GONE);
        }

        if(!MonitorActivity.isBPSwitch()){
            holder.SystolicTV.setVisibility(View.GONE);
            holder.DiastolicTV.setVisibility(View.GONE);
            holder.BPEffectiveDateTV.setVisibility(View.GONE);
            holder.BPTV1.setVisibility(View.GONE);
            holder.BPTV2.setVisibility(View.GONE);
            holder.BPTV3.setVisibility(View.GONE);

        }

        /*
         * This is where we highlight the cholesterol Values that is above the average of the monitored
         * patients.
         */
        if(finalChol > AverageCholesterol){
            holder.CholLevelTV.setText(chol);
            holder.CholLevelTV.setTextColor(Color.parseColor("#FF0000"));
        }
        else {
            holder.CholLevelTV.setText(chol);
        }

        /*
         * This is where we highlight the Blood pressure values if it is above than the value specified
         * by the Health Practitioner.
         */
//      Systolic Blood Pressure
        if(systolicHashParse > MonitorActivity.getSYSTOLICBP()){
            holder.SystolicTV.setText(systolicHash);
            holder.SystolicTV.setTextColor(Color.parseColor("#800080"));
            highSystolicHash.put(patientID, monitoredPatientListHash.get(patientID));
        }
        else {
            holder.SystolicTV.setText(systolicHash);
        }

        // Diastolic Blood Pressure
        if(diastolicHashParse > MonitorActivity.getDIASTOLICBP()){
            holder.DiastolicTV.setText(diastolicHash);
            holder.DiastolicTV.setTextColor(Color.parseColor("#800080"));
        }
        else {
            holder.DiastolicTV.setText(diastolicHash);
        }



        /*
         * This method is used to delete a patient from the list of monitored Patients
         */
        holder.DeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Stopped Monitoring "+ patientname +", click Refresh", Toast.LENGTH_SHORT).show();
                removeItem(monitoredPatientListHash, patientID);
            }
        });

        /*
         * This method is used to obtain the details of each patient upon click
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonitorActivity.setDetails(patientID, context);
            }
        });

    }


    /**
     * This Function is used to obtain the Average Cholesterol Level of all the Monitored Patients
     * @param patientListHash HashMap of Monitored Patients
     * @return Average Cholesterol Value
     */
    private double getAverageReadings(HashMap<String, Patient> patientListHash){
        ArrayList<Double> readingLevels = new ArrayList<>();

        final Object[] keys = patientListHash.keySet().toArray();

        String readingLevel = "";

        for(int i = 0; i<patientListHash.size(); i++){
                readingLevel = patientListHash.get(keys[i]).getCholesterol();

                readingLevel = readingLevel.replaceAll("[^\\d\\.]","");
                double numericChol = Double.parseDouble(readingLevel);
                readingLevels.add(numericChol);
        }

        double total = 0.0;

        for(int i = 0; i<readingLevels.size(); i++){
            total += readingLevels.get(i);
        }

        return total/readingLevels.size();
    }

    /**
     * This function is used to remove a patient from the HashMap of Monitored Patients
     * @param monitoredPatients HashMap of Monitored Patients
     * @param patientID Patient ID
     */
    private void removeItem(HashMap<String, Patient> monitoredPatients, String patientID){
        monitoredPatients.remove(patientID);
    }

    @Override
    public int getItemCount() {
        return monitoredPatientListHash.size();
    }

    /**
     * This method is used to update the values
     * @param o Observable
     * @param arg Argument
     */
    @Override
    public void update(Observable o, Object arg) {
        Log.d("timer", "time is up!");
        ObservationHandler.getObservation("Update", 2, "Chol", false, MainActivity.getMonitoredPatients(), MainActivity.context, MainActivity.getRecyclerView());
        ObservationHandler.getObservation("Update", 2, "BP", false, MainActivity.getMonitoredPatients(), MainActivity.context, MainActivity.getRecyclerView());
    }


    /**
     * Inner Class that will be used to obtain references to the views
     */
    public class MonitorListView extends RecyclerView.ViewHolder{

        private TextView PatientTV;
        private TextView CholLevelTV;
        private TextView CholEffectiveDateTV;
        private ImageView DeleteView;
        private TextView SystolicTV;
        private TextView DiastolicTV;
        private TextView BPEffectiveDateTV;
        private TextView CholTV1;
        private TextView CholTV2;
        private TextView BPTV1;
        private TextView BPTV2;
        private TextView BPTV3;


        public MonitorListView(@NonNull View itemView) {
            super(itemView);
            PatientTV = itemView.findViewById(R.id.monitor_PatientName);
            CholLevelTV = itemView.findViewById(R.id.monitor_cholLevel);
            CholEffectiveDateTV = itemView.findViewById(R.id.cholEffectiveDateTV);
            DeleteView = itemView.findViewById(R.id.image_delete);
            SystolicTV = itemView.findViewById(R.id.systolicTV);
            DiastolicTV = itemView.findViewById(R.id.diastolicTV);
            BPEffectiveDateTV = itemView.findViewById(R.id.BPeffectiveDateTV);
            CholTV1 = itemView.findViewById(R.id.textView3);
            CholTV2 = itemView.findViewById(R.id.textView4);
            BPTV1 = itemView.findViewById(R.id.textView5);
            BPTV2 = itemView.findViewById(R.id.textView6);
            BPTV3 = itemView.findViewById(R.id.textView7);


        }

    }
}
