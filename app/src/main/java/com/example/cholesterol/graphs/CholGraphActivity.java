/**
 * Written by Bee Khee Siang & Aqeel Ahlam Rauf
 * */

package com.example.cholesterol.graphs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.example.cholesterol.Adapters.MonitorAdapter;
import com.example.cholesterol.Objects.Patient;
import com.example.cholesterol.R;
import com.example.cholesterol.ServerCalls.ObservationHandler;
import com.example.cholesterol.UserInterfaces.MainActivity;
import com.example.cholesterol.UserInterfaces.MonitorActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CholGraphActivity extends AppCompatActivity {

    AnyChartView anyChartView;
    Cartesian column;
    Handler handler;
    private static HashMap<String, Patient> monitoredPatientsObtainedMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Cholesterol Graph");
        setContentView(R.layout.activity_graph);
        anyChartView = findViewById(R.id.any_chart_view);
        setupBarChart();

    }

    /**
     * This function is used to set up the Line Chart based on which patient has been selected.
     */
    public void setupBarChart(){

//      Array list of data points
        List<DataEntry> data = new ArrayList<>();

        monitoredPatientsObtainedMap = MainActivity.getMonitoredPatients();
        column = AnyChart.column();
        handler = new Handler();

        if(monitoredPatientsObtainedMap.isEmpty()){
            Toast.makeText(this, "No patients to Monitor", Toast.LENGTH_LONG).show();
        }

        final Object[] keys = monitoredPatientsObtainedMap.keySet().toArray();

        for(int i=0; i<monitoredPatientsObtainedMap.size(); i++){
            final String chol = monitoredPatientsObtainedMap.get(keys[i]).getCholesterol();
            String numericChol = chol.replaceAll("[^\\d\\.]", "");
            double finalChol = Double.parseDouble(numericChol);
            final String name = monitoredPatientsObtainedMap.get(keys[i]).getName();
//          To make sure we only get put values with > 0
            if(finalChol > 0){
                data.add(new ValueDataEntry(name, finalChol));
            }
        }

        column.data(data);
        anyChartView.setChart(column);
        column.labels().enabled(true);

//      Padding Between Graphs
        column.barGroupsPadding(2);

        column.animation(true);
        column.height();
        column.title("Total Cholesterol mg/dL");
        column.tooltip().format("Cholesterol Level: {%value} mg/dL");

        final int delayMillis = 10000;
        final Runnable runnable = new Runnable() {
            public void run() {
                updateBarChart();

                handler.postDelayed(this, delayMillis);
            }
        };
        handler.postDelayed(runnable, delayMillis);
    }

    /**
     * This Function will update the Graph based on N-Value
     */
    public void updateBarChart() {

        ObservationHandler.getObservation("Update", 1, "Chol", true, MainActivity.getMonitoredPatients(), MainActivity.context, MainActivity.getRecyclerView());

        List<DataEntry> data = new ArrayList<>();

        final Object[] keys = monitoredPatientsObtainedMap.keySet().toArray();

        for(int i=0; i<monitoredPatientsObtainedMap.size(); i++){
            final String chol = monitoredPatientsObtainedMap.get(keys[i]).getCholesterol();
            String numericChol = chol.replaceAll("[^\\d\\.]", "");
            double finalChol = Double.parseDouble(numericChol);
            final String name = monitoredPatientsObtainedMap.get(keys[i]).getName();

//          To make sure we only get put values with >0
            if(finalChol>0){
                data.add(new ValueDataEntry(name, finalChol));
            }
        }

        column.data(data);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        handler.removeCallbacksAndMessages(null);
        MonitorAdapter monitorAdapter = new MonitorAdapter(MainActivity.getMonitoredPatients(), MainActivity.context);
        MonitorActivity.refresh(monitorAdapter);
    }

}
