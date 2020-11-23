/**
 * Written by Bee Khee Siang & Aqeel Ahlam Rauf
 * */

package com.example.cholesterol.UserInterfaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cholesterol.Adapters.BPMonitorAdapter;
import com.example.cholesterol.Adapters.MonitorAdapter;
import com.example.cholesterol.Observable.NTimer;
import com.example.cholesterol.R;
import com.example.cholesterol.ServerCalls.ObservationHandler;
import com.example.cholesterol.graphs.BPGraphActivity;


public class BPMonitorActivity extends AppCompatActivity {

    private static RecyclerView bpMonitorRecyclerView;

    public static Context context;

    public static RecyclerView getBPMonitorRecyclerView() {
        return bpMonitorRecyclerView;
    }

    private static NTimer nTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp_monitor);

        getSupportActionBar().setTitle("Blood Pressure Monitor");

//      This is used to obtain the recyclerView
        bpMonitorRecyclerView = findViewById(R.id.bp_recycler);
        bpMonitorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ObservationHandler.getObservation("firstCall", 1, "XBP", false, MonitorAdapter.getHighSystolic(), this, bpMonitorRecyclerView);

    }

    /**
     * This function will be invoked when the start button is clicked. It is used to Monitor
     * patients that have high "Systolic" blood Pressures.
     * @param view viewObject: Start Button
     */
    public void systolicStartBtn(View view){

        if (MonitorAdapter.getHighSystolic().isEmpty()){
            Toast.makeText(this, "Patients don't have high Systolic blood Pressure", Toast.LENGTH_LONG).show();
        }
        else {
            stopTimer();
            final BPMonitorAdapter bpMonitorAdapter = new BPMonitorAdapter(MonitorAdapter.getHighSystolic(), this);
            bpMonitorRecyclerView.setAdapter(bpMonitorAdapter);

            int NValue = Integer.parseInt(MonitorActivity.NRefresh.getText().toString());
            NTimer.setN(NValue);
            NTimer.resetN();
            nTimer = new NTimer();
            nTimer.addObserver(bpMonitorAdapter);
            nTimer.startTimer();
        }
    }

    public static void refresh(BPMonitorAdapter bpMonitorAdapter) {
        bpMonitorRecyclerView.setAdapter(bpMonitorAdapter);
        NTimer.resetN();
        nTimer = new NTimer();
        nTimer.addObserver(bpMonitorAdapter);
        nTimer.startTimer();
    }

    public static void stopTimer() {
        if (nTimer != null) {
            nTimer.task.cancel();
        }
    }

    public void visualizeBloodPressureBtn(View view){
        if(!BPMonitorAdapter.getClickListener()){
            Toast.makeText(this, "Please Select a patient to monitor", Toast.LENGTH_SHORT).show();
        }
        else {
            stopTimer();
            Intent intent = new Intent(this, BPGraphActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        stopTimer();
        MonitorAdapter monitorAdapter = new MonitorAdapter(MainActivity.getMonitoredPatients(), MainActivity.context);
        MonitorActivity.refresh(monitorAdapter);
    }


}
