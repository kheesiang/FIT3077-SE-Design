
package com.example.cholesterol.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cholesterol.Objects.Patient;
import com.example.cholesterol.R;
import com.google.android.material.snackbar.Snackbar;
import java.util.HashMap;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientListView> {

//  This hashmap holds a list of all patients
    private HashMap<String, Patient> patientListHash;
//  This hashmap holds a list of selected patients
    private HashMap<String, Patient> monitoredPatients;

    /**
     * Constructor for PatientListAdapter
     * @param patientListHashA HashMap of Patients
     * @param monitoredPatients HashMap of Monitored Patients
     */
    public PatientListAdapter(HashMap<String, Patient> patientListHashA, HashMap<String, Patient> monitoredPatients){
        this.patientListHash = patientListHashA;
        this.monitoredPatients = monitoredPatients;
    }


    @NonNull
    @Override
    public PatientListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View patView = inflater.inflate(R.layout.patients, parent, false);

        PatientListView view = new PatientListView(patView);

        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull PatientListView holder, final int position) {

//      Here I obtain a the list of keys and convert it into an array in order to use "position" to access the correct card
        final Object[] keys = patientListHash.keySet().toArray();

        final String patientname = patientListHash.get(keys[position]).getName();
        final String patientID = patientListHash.get(keys[position]).getPatientID();

        holder.patientID.setText(patientID);
        holder.patientName.setText(patientname);

//      This will add a patient to the montiored HashMap when we click it
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            /**
             * This function is used to add patients to be Monitored
             * @param v
             */
            @Override
            public void onClick(View v) {

                Patient patient = new Patient(patientID, patientname, "0.0","0.0", "0.0");

//              Here we move the selected Patients into a new HashMap
                monitoredPatients.put(patientID, patient);

                Snackbar.make(v,"You have Selected: " + monitoredPatients.get(keys[position]).getName(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientListHash.size();
    }

    /**
     * Inner Class that will be used to obtain references to the views
     */
    public class PatientListView extends RecyclerView.ViewHolder {
        TextView patientID;
        TextView patientName;

        public PatientListView(@NonNull View itemView) {
            super(itemView);
            patientID = itemView.findViewById(R.id.patient_id);
            patientName = itemView.findViewById(R.id.patient_name);

        }
    }
}
