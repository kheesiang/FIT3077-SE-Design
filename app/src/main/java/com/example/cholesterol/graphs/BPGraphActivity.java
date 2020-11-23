package com.example.cholesterol.graphs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.cholesterol.Adapters.BPMonitorAdapter;
import com.example.cholesterol.Adapters.MonitorAdapter;
import com.example.cholesterol.Objects.Patient;
import com.example.cholesterol.R;
import com.example.cholesterol.ServerCalls.ObservationHandler;
import com.example.cholesterol.UserInterfaces.BPMonitorActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class BPGraphActivity extends AppCompatActivity {
    private AnyChartView lineChart;
    Handler handler;
    Cartesian cartesian;
    Line series;

    HashMap<String, Patient> SystolicBP = new HashMap<>();
    HashMap<Integer, ArrayList<String>> XlatestBP = new HashMap<>();
    Object[] keySetSystolic;
    Object[] keySetXlatest;
    ArrayList<String> value = new ArrayList<>();
    String PatientName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_graph_monitor_b_p);
        getSupportActionBar().setTitle("Blood Pressure Graph");
        lineChart = findViewById(R.id.any_chart_view_BP);
        setLineChart();

    }

    /**
     * This function is used to set up the Line Chart based on which patient has been selected.
     */
    public void setLineChart(){
//      Array list of data points
        ArrayList<DataEntry> entries = new ArrayList<>();

        SystolicBP = MonitorAdapter.getHighSystolic();
        XlatestBP = BPMonitorAdapter.getXLatestBP();
        PatientName = BPMonitorAdapter.getPatientName();

        for (int i = 0; i < XlatestBP.size(); i++) {
            keySetXlatest = XlatestBP.keySet().toArray();
            assert keySetXlatest != null;
            value = XlatestBP.get(keySetXlatest[i]);
            assert value != null;
            Double BP = Double.parseDouble(value.get(1).replaceAll("[^\\d\\.]", ""));
            entries.add(new ValueDataEntry(String.valueOf(i), BP));
        }

        cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title(PatientName);
        cartesian.yAxis(0).title("Blood Pressure");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);


        series = cartesian.line(entries);
        series.name("Systolic BP");
        series.hovered().markers().enabled(true);
        series.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series.tooltip()
                .position("right")
                .offsetX(5d)
                .offsetY(5d);
        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);
        lineChart.setChart(cartesian);

        final int delayMillis = 10000;
        final Runnable runnable = new Runnable() {
            public void run() {
                updateLineGraph();

                handler.postDelayed(this, delayMillis);
            }
        };
        handler.postDelayed(runnable, delayMillis);

    }


    /**
     * This Function will update the Graph based on N-Value
     */
    public void updateLineGraph() {

        ObservationHandler.getObservation("Update", 1, "XBP", true, MonitorAdapter.getHighSystolic(), BPMonitorActivity.context, BPMonitorActivity.getBPMonitorRecyclerView());

        ArrayList<DataEntry> entries = new ArrayList<>();
        SystolicBP = MonitorAdapter.getHighSystolic();
        keySetSystolic = SystolicBP.keySet().toArray();

        assert keySetSystolic != null;
        for (Object KeySystol : keySetSystolic) {
            XlatestBP = BPMonitorAdapter.getXLatestBP();
            PatientName = SystolicBP.get(KeySystol).getName();

            for (int i = 0; i < XlatestBP.size(); i++) {
                keySetXlatest = XlatestBP.keySet().toArray();
                value = XlatestBP.get(keySetXlatest[i]);
                Double BP = Double.parseDouble(value.get(1).replaceAll("[^\\d\\.]", ""));
                entries.add(new ValueDataEntry(String.valueOf(i), BP));
            }
        }
        cartesian.removeAllSeries();
        series = cartesian.line(entries);
        series.name("Systolic BP");
        series.hovered().markers().enabled(true);
        series.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        handler.removeCallbacksAndMessages(null);
        BPMonitorAdapter bpMonitorAdapter = new BPMonitorAdapter(MonitorAdapter.getHighSystolic(), this);
        BPMonitorActivity.refresh(bpMonitorAdapter);
    }
}