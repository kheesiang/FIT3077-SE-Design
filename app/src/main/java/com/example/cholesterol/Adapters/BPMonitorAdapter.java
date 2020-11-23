/**
* Written by Bee Khee Siang & Aqeel Ahlam Rauf
* */

package com.example.cholesterol.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cholesterol.Objects.Patient;
import com.example.cholesterol.R;
import com.example.cholesterol.ServerCalls.ObservationHandler;
import com.example.cholesterol.UserInterfaces.BPMonitorActivity;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class BPMonitorAdapter extends RecyclerView.Adapter<BPMonitorAdapter.BPMonitorListView> implements Observer {


    private Context context;
    private HashMap<String, Patient> highSystolic;

    private static HashMap<Integer, ArrayList<String>> XLatestBP;

    private static String patientName;

    private static boolean ClickListener = false;

    /**
     * Accessors & Mutator for Variables that will be used/needed by other classes
     */

    public static String getPatientName() {
        return patientName;
    }

    public static void setPatientName(String patientName) {
        BPMonitorAdapter.patientName = patientName;
    }

    public static boolean getClickListener(){
        return ClickListener;
    }

    public void setClickListener(boolean clickListener){
        ClickListener = clickListener;
    }

    public static HashMap<Integer, ArrayList<String>> getXLatestBP() {
        return XLatestBP;
    }

    public void setXLatestBP(HashMap<Integer, ArrayList<String>> test1) {
        this.XLatestBP = test1;
    }


    /**
     * Constructor for BP Monitor Adapter
     * @param highSystolic Hashmap of patients with High Systolic Pressure
     * @param context Activity Context
     */
    public BPMonitorAdapter(HashMap<String, Patient> highSystolic, Context context) {
        this.highSystolic = highSystolic;
        this.context = context;
    }

    @NonNull
    @Override
    public BPMonitorListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View BPMonitorView = inflater.inflate(R.layout.bp_monitor_card, parent, false);
        BPMonitorListView view = new BPMonitorListView(BPMonitorView);

        return view;

    }

    @Override
    public void onBindViewHolder(@NonNull BPMonitorListView holder, int position) {
//      Here is where you bind the view with the data
        final Object[] keys = highSystolic.keySet().toArray();

        final String patientname = highSystolic.get(keys[position]).getName();
        final String patientID = highSystolic.get(keys[position]).getPatientID();

        final String systolic1 = highSystolic.get(keys[position]).getFormattedXLatestBP(0);
        final String systolic2 = highSystolic.get(keys[position]).getFormattedXLatestBP(1);
        final String systolic3 = highSystolic.get(keys[position]).getFormattedXLatestBP(2);
        final String systolic4 = highSystolic.get(keys[position]).getFormattedXLatestBP(3);
        final String systolic5 = highSystolic.get(keys[position]).getFormattedXLatestBP(4);

        holder.patientName.setText(patientname);
        holder.SystolicValue1.setText(systolic1);
        holder.SystolicValue2.setText(systolic2);
        holder.SystolicValue3.setText(systolic3);
        holder.SystolicValue4.setText(systolic4);
        holder.SystolicValue5.setText(systolic5);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Preparing Visualization for: " + patientname + ", Click Visualize to begin", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                setXLatestBP(highSystolic.get(patientID).getXLatestBP());
                setPatientName(patientname);
                setClickListener(true);
            }
        });

    }

    @Override
    public int getItemCount() {
        return highSystolic.size();

    }

    /**
     * This method is used to update the values
     * @param o Observable
     * @param arg Argument
     */
    @Override
    public void update(Observable o, Object arg) {
        Log.d("timer", "time is up!");
        ObservationHandler.getObservation("Update", 1, "XBP", false, MonitorAdapter.getHighSystolic(), BPMonitorActivity.context, BPMonitorActivity.getBPMonitorRecyclerView());
    }


    /**
     * Inner Class that will be used to obtain references to the views
     */
    public class BPMonitorListView extends RecyclerView.ViewHolder{
        TextView patientName;
        TextView SystolicValue1;
        TextView SystolicValue2;
        TextView SystolicValue3;
        TextView SystolicValue4;
        TextView SystolicValue5;

        public BPMonitorListView(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.bp_patient);
            SystolicValue1 = itemView.findViewById(R.id.bp_systolic1);
            SystolicValue2 = itemView.findViewById(R.id.bp_systolic2);
            SystolicValue3 = itemView.findViewById(R.id.bp_systolic3);
            SystolicValue4 = itemView.findViewById(R.id.bp_systolic4);
            SystolicValue5 = itemView.findViewById(R.id.bp_systolic5);


        }
    }
}
