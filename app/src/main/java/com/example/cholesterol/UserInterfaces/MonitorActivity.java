package com.example.cholesterol.UserInterfaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cholesterol.Adapters.MonitorAdapter;
import com.example.cholesterol.Observable.NTimer;
import com.example.cholesterol.Objects.Patient;
import com.example.cholesterol.R;
import com.example.cholesterol.ServerCalls.PatientData;
import com.example.cholesterol.graphs.CholGraphActivity;

import java.util.HashMap;

public class MonitorActivity extends AppCompatActivity {

    private static RecyclerView monitorRecyclerView;
    private static HashMap<String, Patient> monitored = new HashMap<>();
    private static TextView name;
    private static TextView birthdate;
    private static TextView gender;
    private static TextView address;
    public static EditText NRefresh;
    private static EditText SystolicBP;
    private static EditText DiastolicBP;
    private static Switch cholesterolSwitch;
    private static Switch BloodPressureSwitch;
    private static NTimer nTimer;

//  This is to get the status of the switch
    private static boolean BPSwitch = true;
    public static boolean isBPSwitch() {
        return BPSwitch;
    }
    public static void setBPSwitch(boolean BPSwitch) {
        MonitorActivity.BPSwitch = BPSwitch;
    }

    private static boolean cholSwitch = true;
    public static boolean isCholSwitch() {
        return cholSwitch;
    }
    public static void setCholSwitch(boolean cholSwitch) {
        MonitorActivity.cholSwitch = cholSwitch;
    }

    private static double SYSTOLICBP;
    private static double DIASTOLICBP;

    public static double getSYSTOLICBP() {
        return SYSTOLICBP;
    }

    public static void setSYSTOLICBP(double SYSTOLICBP) {
        MonitorActivity.SYSTOLICBP = SYSTOLICBP;
    }

    public static double getDIASTOLICBP() {
        return DIASTOLICBP;
    }

    public static void setDIASTOLICBP(double DIASTOLICBP) {
        MonitorActivity.DIASTOLICBP = DIASTOLICBP;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      Inflates the layout:
        setContentView(R.layout.activity_monitor);

//      This will set the title of the toolbar
        getSupportActionBar().setTitle("Monitored Patients");

//      This is used to obtain the recyclerView
        monitorRecyclerView = findViewById(R.id.monitor_recycler);
        monitorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//      This is used to obtain the TextViews
        name = findViewById(R.id.monitor_name);
        birthdate = findViewById(R.id.monitor_birthdate);
        gender = findViewById(R.id.monitor_gender);
        address = findViewById(R.id.monitor_address);
        NRefresh = findViewById(R.id.refresh);
        SystolicBP = findViewById(R.id.monitor_systolicBP);
        DiastolicBP = findViewById(R.id.monitor_diastolicBP);

        cholesterolSwitch = findViewById(R.id.cholSwitch);
        BloodPressureSwitch = findViewById(R.id.BPSwitch);
    }

    /**
     * This is function that will be invoked when the "Visualize" button is clicked
     * @param view
     */
    public void graphButton(View view){
        stopTimer();
        Intent intent = new Intent(this, CholGraphActivity.class);
        startActivity(intent);
    }

    /**
     * This is function that will be invoked when the "Systolic" button is clicked
     * @param view
     */
    public void BPMonitorButton(View view){
        stopTimer();
        Intent intent = new Intent(this, BPMonitorActivity.class);
        startActivity(intent);
    }


    /**
     * This is function that will be invoked when the start button is clicked
     * @param view viewObject: Start Button
     */
    public void RefreshBtn(View view) {
        stopTimer();

//        String N_Value = NRefresh.getText().toString();
//        String SystolicEdit = SystolicBP.getText().toString();
//        String DiastolicEdit = DiastolicBP.getText().toString();

//      FOR TESTING PURPOSES
        String N_Value = "10";
        String SystolicEdit = "1";
        String DiastolicEdit = "2000";


        /*
        Here is where we set the state of the switch, Afterwards we can use this to display values
        of Cholesterol or Blood Pressure Interchangeably.
        */
        cholesterolSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setCholSwitch(true);
                }
                else{
                    setCholSwitch(false);
                }
            }
        });

        BloodPressureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setBPSwitch(true);
                }
                else {
                    setBPSwitch(false);
                }
            }
        });


//      If we don't put a value for:
        if(N_Value.isEmpty()){
            Toast.makeText(this, "Please enter a Refresh Value", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(SystolicEdit.isEmpty() || DiastolicEdit.isEmpty()){
            Toast.makeText(this, "Please enter a BP Value", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
//          Obtaining the HashMap of Monitored Patients from MainActivity
            monitored = MainActivity.getMonitoredPatients();

            Log.d("monitor length", String.valueOf(monitored.size()));

//          Passing the HashMap to the MonitorAdapter to populate the RecyclerView
            final MonitorAdapter monitorAdapter = new MonitorAdapter(monitored, this);
            monitorRecyclerView.setAdapter(monitorAdapter);

//          This is used to update the cholesterol levels at N_value (second(s)) intervals
            int NValue = Integer.parseInt(N_Value);
            NTimer.setN(NValue);
            NTimer.resetN();
//            NTimer nTimer = new NTimer();
            nTimer = new NTimer();
            nTimer.addObserver(monitorAdapter);
            nTimer.startTimer();

            double Systolic = Double.parseDouble(SystolicEdit);
            double Diastolic = Double.parseDouble(DiastolicEdit);

//          Here we set the Systolic & Diastolic BP values which was entered by the Practitioner.
            setSYSTOLICBP(Systolic);
            setDIASTOLICBP(Diastolic);
        }
    }

    /**
     * This function is used to subscribe to the observable and starts the timer
     * @param monitorAdapter The adapter for the recycler View
     */
    public static void refresh(MonitorAdapter monitorAdapter) {
        monitorRecyclerView.setAdapter(monitorAdapter);
        NTimer.resetN();
        nTimer = new NTimer();
        nTimer.addObserver(monitorAdapter);
        nTimer.startTimer();
    }


    public static void stopTimer() {
        if (nTimer != null) {
            nTimer.task.cancel();
        }
    }

    /**
     * This function is used to obtain the details of a patient once it has been selected
     * @param patientID Patient Identification
     * @param context Context
     */
    public static void setDetails(String patientID, Context context){
        monitored = MainActivity.getPatientDetailsMap();
        PatientData.getDetailedPatient(monitored, patientID, context);

        Patient current = monitored.get(patientID);
        if(current.getGender() != null){
            String Name = current.getName();
            String Birthdate = current.getBirthDate();
            String Gender = current.getGender();
            String addressLine = current.getAddressLine();
            String city = current.getCity();
            String state = current.getState();
            String postalCode = current.getPostalCode();
            String country = current.getCountry();
            String fullAddress = addressLine + ", " + city + ", " + state + ", " + postalCode + ", " + country;

//          Here we bind the details to the TextView
            name.setText(Name);
            birthdate.setText(Birthdate);
            gender.setText(Gender);
            address.setText(fullAddress);

        } else {
            String Waiting = "Waiting for server";
            name.setText(Waiting);
            birthdate.setText(Waiting);
            gender.setText(Waiting);
            address.setText(Waiting);
        }
    }
}
