package com.example.solaris;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.UUID;


import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PrefDialog.PrefListener {
    public ProgressBar bat1_meter, bat2_meter;
    public TextView bat1_labl, bat2_labl, testMode, powText, coText;
    public Button total_pow, co2, supply_text;
    public LinearLayout source_line, supply_line, line_bat1, line2_bat1, line3_bat1, line4_bat1, line_bat2, line2_bat2, line3_bat2, line4_bat2,
            source_lineo, supply_lineo, line_bat1o, line2_bat1o, line3_bat1o, line4_bat1o, line_bat2o, line2_bat2o, line3_bat2o, line4_bat2o;
    public ImageView bolt1, bolt2;
    public EditText maxChargeTxt, dummy;
    public int charge1, maxCharge, charge2, supply_int, total_pow_data, bat_charge, bat_supply;
    public String supply, deviceAddress, currentMode, statusBat1, statusBat2;
    public SharedPreferences sharedPrefs;
    public float x1, x2, y1, y2;
    public Boolean turnOff;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    Toolbar toolbar;
    int counter;
    MutableLiveData <String> test = new MutableLiveData<String>();;

    UUID myuuid = UUID.fromString("e65b27dc-df81-11ed-b5ea-0242ac120002");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // declaring relevant UI elements
        bat1_meter = findViewById(R.id.bat1_meter);
        bat1_labl = findViewById(R.id.bat1_labl);
        bat2_meter = findViewById(R.id.bat2_meter);
        bat2_labl = findViewById(R.id.bat2_labl);
        source_line = findViewById(R.id.linearLayout);
        source_lineo = findViewById(R.id.linearLayouto);
        line_bat1 = findViewById(R.id.linearLayout1);
        line2_bat1 = findViewById(R.id.linearLayout2);
        line_bat1o = findViewById(R.id.linearLayout1o);
        line2_bat1o = findViewById(R.id.linearLayout2o);
        line_bat2 = findViewById(R.id.linearLayout3);
        line2_bat2 = findViewById(R.id.linearLayout4);
        line_bat2o = findViewById(R.id.linearLayout3o);
        line2_bat2o = findViewById(R.id.linearLayout4o);
        supply_line = findViewById(R.id.linearLayout7);
        supply_lineo = findViewById(R.id.linearLayout7o);
        line3_bat1 = findViewById(R.id.linearLayout8);
        line4_bat1 = findViewById(R.id.linearLayout9);
        line3_bat1o = findViewById(R.id.linearLayout8o);
        line4_bat1o = findViewById(R.id.linearLayout9o);
        line3_bat2 = findViewById(R.id.linearLayout10);
        line4_bat2 = findViewById(R.id.linearLayout11);
        line3_bat2o = findViewById(R.id.linearLayout10o);
        line4_bat2o = findViewById(R.id.linearLayout11o);
        total_pow = findViewById(R.id.total_pow1);
        co2 = findViewById(R.id.co21);
        bolt1 = findViewById(R.id.imageView7);
        bolt2 = findViewById(R.id.imageView8);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_bar);
        testMode = findViewById(R.id.testMode);
        supply_text = findViewById(R.id.supply_text);
        powText = findViewById(R.id.textPow);
        coText = findViewById(R.id.textCO);
        dummy = findViewById(R.id.dummy);


        sharedPrefs = getSharedPreferences("Prefs", MODE_PRIVATE);

        bat_charge = sharedPrefs.getInt("Charge Decision", 0);
        bat_supply = sharedPrefs.getInt("Supply Decision", 0);

        // Connect to database and read the System child branch
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("System");

        //navigation bar logic
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open_string, R.string.navigation_close_string);
        navigationView.setNavigationItemSelectedListener(this);

        // Read current mode on app startup
        myRef.child("Mode").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    currentMode = String.valueOf(task.getResult().getValue());
                    testMode.setText(currentMode);
                    switch(currentMode){
                        case "Normal":
                        case "Custom":
                            testMode.setTextColor(Color.WHITE);
                            break;
                        case "Max Power":
                            testMode.setTextColor(Color.RED);
                            break;
                    }
                }
            }
        });

        // Update current mode when value changes
        myRef.child("Mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                currentMode = dataSnapshot.getValue(String.class);
                testMode.setText(currentMode);
                switch(currentMode){
                    case "Normal":
                    case "Custom":
                        testMode.setTextColor(Color.WHITE);
                        break;
                    case "Max Power":
                        testMode.setTextColor(Color.RED);
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("check", "Failed to read value.", error.toException());
            }
        });

        // Read power generated on app startup
        myRef.child("Lifetime Power Generated").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    total_pow_data = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                    double co2_data = (total_pow_data * 3.142 * 173.12)/1000/1000;
                    BigDecimal bd = new BigDecimal(co2_data);
                    MathContext m = new MathContext(4);
                    bd = bd.round(m);
                    co2_data = bd.doubleValue();
                    powText.setText(String.valueOf(total_pow_data) + " W\n          ");
                    coText.setText(String.valueOf(co2_data) + " t\n           ");
                }
            }
        });

        // Update power data when value changes
        myRef.child("Lifetime Power Generated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                total_pow_data = dataSnapshot.getValue(Integer.class);
                double co2_data = (total_pow_data * 3.142 * 173.12)/1000/1000;
                BigDecimal bd = new BigDecimal(co2_data);
                MathContext m = new MathContext(4);
                bd = bd.round(m);
                co2_data = bd.doubleValue();
                powText.setText(String.valueOf(total_pow_data) + " W\n           ");
                coText.setText(String.valueOf(co2_data) + " t\n           ");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("check", "Failed to read value.", error.toException());
            }
        });

        // Read power consumed on app startup
        myRef.child("Power Consumed").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    supply_int = Integer.parseInt(String.valueOf(task.getResult().getValue())); //data placeholders
                    supply_text.setText(String.valueOf(supply_int) + " W\n          ");

                }
            }
        });

        // Update power consumed when value changes
        myRef.child("Power Consumed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                supply_int = dataSnapshot.getValue(Integer.class);
                supply_text.setText(String.valueOf(supply_int) + " W\n          ");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("check", "Failed to read value.", error.toException());
            }
        });

        // Read battery 1 charge level on app startup
        myRef.child("Battery 1").child("Charge").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    charge1 = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                    bat1_meter.setProgress(charge1);
                    bat1_labl.setText(task.getResult().getValue() + "%");
                }
            }
        });

        // Update battery 1 charge level when value changes
        myRef.child("Battery 1").child("Charge").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                charge1 = dataSnapshot.getValue(Integer.class);
                bat1_meter.setProgress(charge1);
                bat1_labl.setText(charge1 + "%");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("check", "Failed to read value.", error.toException());
            }
        });

        // Read battery 2 charge level on app startup
        myRef.child("Battery 2").child("Charge").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    charge2 = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                    bat2_meter.setProgress(charge2);
                    bat2_labl.setText(task.getResult().getValue() + "%");
                }
            }
        });

        // Update battery 2 charge level when value changes
        myRef.child("Battery 2").child("Charge").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                charge2 = dataSnapshot.getValue(Integer.class);
                bat2_meter.setProgress(charge2);
                bat2_labl.setText(charge2 + "%");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("check", "Failed to read value.", error.toException());
            }
        });

        turnOff = false;
        if(turnOff){
            source_line.setVisibility(View.INVISIBLE);
            line_bat1.setVisibility(View.INVISIBLE);
            line2_bat1.setVisibility(View.INVISIBLE);
            line_bat1o.setVisibility(View.VISIBLE);
            line2_bat1o.setVisibility(View.VISIBLE);

            line_bat2.setVisibility(View.INVISIBLE);
            line2_bat2.setVisibility(View.INVISIBLE);
            line_bat2o.setVisibility(View.VISIBLE);
            line2_bat2o.setVisibility(View.VISIBLE);

            supply_line.setVisibility(View.INVISIBLE);
            line3_bat1.setVisibility(View.INVISIBLE);
            line4_bat1.setVisibility(View.INVISIBLE);
            line3_bat1o.setVisibility(View.VISIBLE);
            line4_bat1o.setVisibility(View.VISIBLE);

            line3_bat2.setVisibility(View.INVISIBLE);
            line4_bat2.setVisibility(View.INVISIBLE);
            line3_bat2o.setVisibility(View.VISIBLE);
            line4_bat2o.setVisibility(View.VISIBLE);

            bolt1.setVisibility(View.INVISIBLE);
            bolt2.setVisibility(View.INVISIBLE);

            bat1_meter.setSecondaryProgress(0);
            bat2_meter.setSecondaryProgress(0);
        }
        else {
            // Highlight UI paths relevant to Battery 1 status
            myRef.child("Battery 1").child("Status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        statusBat1 = String.valueOf(task.getResult().getValue());
                        switch (statusBat1) {
                            case "Charging":
                                source_line.setVisibility(View.VISIBLE);
                                source_lineo.setVisibility(View.INVISIBLE);
                                line_bat1.setVisibility(View.VISIBLE);
                                line2_bat1.setVisibility(View.VISIBLE);
                                line_bat1o.setVisibility(View.INVISIBLE);
                                line2_bat1o.setVisibility(View.INVISIBLE);

                                line_bat2.setVisibility(View.INVISIBLE);
                                line2_bat2.setVisibility(View.INVISIBLE);
                                line_bat2o.setVisibility(View.VISIBLE);
                                line2_bat2o.setVisibility(View.VISIBLE);

                                bolt1.setVisibility(View.VISIBLE);
                                bolt2.setVisibility(View.INVISIBLE);

                                bat1_meter.setSecondaryProgress(maxCharge);
                                bat2_meter.setSecondaryProgress(0);
                                break;
                            case "Supplying":

                                supply_line.setVisibility(View.VISIBLE);
                                supply_lineo.setVisibility(View.INVISIBLE);
                                line3_bat1.setVisibility(View.VISIBLE);
                                line4_bat1.setVisibility(View.VISIBLE);
                                line3_bat1o.setVisibility(View.INVISIBLE);
                                line4_bat1o.setVisibility(View.INVISIBLE);

                                line3_bat2.setVisibility(View.INVISIBLE);
                                line4_bat2.setVisibility(View.INVISIBLE);
                                line3_bat2o.setVisibility(View.VISIBLE);
                                line4_bat2o.setVisibility(View.VISIBLE);
                                break;

                            case "Off":
                                source_line.setVisibility(View.INVISIBLE);
                                source_lineo.setVisibility(View.VISIBLE);
                                line_bat1.setVisibility(View.INVISIBLE);
                                line2_bat1.setVisibility(View.INVISIBLE);
                                line_bat1o.setVisibility(View.VISIBLE);
                                line2_bat1o.setVisibility(View.VISIBLE);


                                bolt1.setVisibility(View.INVISIBLE);

                                supply_line.setVisibility(View.INVISIBLE);
                                supply_lineo.setVisibility(View.VISIBLE);
                                line3_bat1.setVisibility(View.INVISIBLE);
                                line4_bat1.setVisibility(View.INVISIBLE);
                                line3_bat1o.setVisibility(View.VISIBLE);
                                line4_bat1o.setVisibility(View.VISIBLE);

                                break;
                        }
                    }
                }
            });
            myRef.child("Battery 1").child("Status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    statusBat1 = dataSnapshot.getValue(String.class);
                    switch (statusBat1) {
                        case "Charging":
                            source_line.setVisibility(View.VISIBLE);
                            source_lineo.setVisibility(View.INVISIBLE);
                            line_bat1.setVisibility(View.VISIBLE);
                            line2_bat1.setVisibility(View.VISIBLE);
                            line_bat1o.setVisibility(View.INVISIBLE);
                            line2_bat1o.setVisibility(View.INVISIBLE);

                            line_bat2.setVisibility(View.INVISIBLE);
                            line2_bat2.setVisibility(View.INVISIBLE);
                            line_bat2o.setVisibility(View.VISIBLE);
                            line2_bat2o.setVisibility(View.VISIBLE);

                            bolt1.setVisibility(View.VISIBLE);
                            bolt2.setVisibility(View.INVISIBLE);

                            bat1_meter.setSecondaryProgress(maxCharge);
                            bat2_meter.setSecondaryProgress(0);
                            break;
                        case "Supplying":

                            supply_line.setVisibility(View.VISIBLE);
                            supply_lineo.setVisibility(View.INVISIBLE);
                            line3_bat1.setVisibility(View.VISIBLE);
                            line4_bat1.setVisibility(View.VISIBLE);
                            line3_bat1o.setVisibility(View.INVISIBLE);
                            line4_bat1o.setVisibility(View.INVISIBLE);

                            line3_bat2.setVisibility(View.INVISIBLE);
                            line4_bat2.setVisibility(View.INVISIBLE);
                            line3_bat2o.setVisibility(View.VISIBLE);
                            line4_bat2o.setVisibility(View.VISIBLE);
                            break;

                        case "Off":
                            source_line.setVisibility(View.INVISIBLE);
                            source_lineo.setVisibility(View.VISIBLE);
                            line_bat1.setVisibility(View.INVISIBLE);
                            line2_bat1.setVisibility(View.INVISIBLE);
                            line_bat1o.setVisibility(View.VISIBLE);
                            line2_bat1o.setVisibility(View.VISIBLE);

                            line_bat2.setVisibility(View.INVISIBLE);
                            line2_bat2.setVisibility(View.INVISIBLE);
                            line_bat2o.setVisibility(View.VISIBLE);
                            line2_bat2o.setVisibility(View.VISIBLE);

                            bolt1.setVisibility(View.INVISIBLE);

                            supply_line.setVisibility(View.INVISIBLE);
                            supply_lineo.setVisibility(View.VISIBLE);
                            line3_bat1.setVisibility(View.INVISIBLE);
                            line4_bat1.setVisibility(View.INVISIBLE);
                            line3_bat1o.setVisibility(View.VISIBLE);
                            line4_bat1o.setVisibility(View.VISIBLE);

                            line3_bat2.setVisibility(View.VISIBLE);
                            line4_bat2.setVisibility(View.VISIBLE);
                            line3_bat2o.setVisibility(View.INVISIBLE);
                            line4_bat2o.setVisibility(View.INVISIBLE);
                            break;
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("check", "Failed to read value.", error.toException());
                }
            });

            // Highlight UI paths relevant to Battery 1 status
            myRef.child("Battery 2").child("Status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        statusBat2 = String.valueOf(task.getResult().getValue());
                        switch (statusBat2) {
                            case "Charging":
                                source_line.setVisibility(View.VISIBLE);
                                source_lineo.setVisibility(View.INVISIBLE);
                                line_bat1.setVisibility(View.INVISIBLE);
                                line2_bat1.setVisibility(View.INVISIBLE);
                                line_bat1o.setVisibility(View.VISIBLE);
                                line2_bat1o.setVisibility(View.VISIBLE);

                                line_bat2.setVisibility(View.VISIBLE);
                                line2_bat2.setVisibility(View.VISIBLE);
                                line_bat2o.setVisibility(View.INVISIBLE);
                                line2_bat2o.setVisibility(View.INVISIBLE);

                                bolt1.setVisibility(View.INVISIBLE);
                                bolt2.setVisibility(View.VISIBLE);

                                bat1_meter.setSecondaryProgress(0);
                                bat2_meter.setSecondaryProgress(maxCharge);
                                break;
                            case "Supplying":
                                supply_line.setVisibility(View.VISIBLE);
                                supply_lineo.setVisibility(View.INVISIBLE);
                                line3_bat1.setVisibility(View.INVISIBLE);
                                line4_bat1.setVisibility(View.INVISIBLE);
                                line3_bat1o.setVisibility(View.VISIBLE);
                                line4_bat1o.setVisibility(View.VISIBLE);

                                line3_bat2.setVisibility(View.VISIBLE);
                                line4_bat2.setVisibility(View.VISIBLE);
                                line3_bat2o.setVisibility(View.INVISIBLE);
                                line4_bat2o.setVisibility(View.INVISIBLE);
                                break;

                            case "Off":
                                source_line.setVisibility(View.INVISIBLE);
                                source_lineo.setVisibility(View.VISIBLE);

                                line_bat2.setVisibility(View.INVISIBLE);
                                line2_bat2.setVisibility(View.INVISIBLE);
                                line_bat2o.setVisibility(View.VISIBLE);
                                line2_bat2o.setVisibility(View.VISIBLE);
                                line3_bat2.setVisibility(View.INVISIBLE);
                                line4_bat2.setVisibility(View.INVISIBLE);
                                line3_bat2o.setVisibility(View.VISIBLE);
                                line4_bat2o.setVisibility(View.VISIBLE);

                                bolt2.setVisibility(View.INVISIBLE);
                                bat2_meter.setSecondaryProgress(0);
                                break;
                        }
                    }
                }
            });

            myRef.child("Battery 2").child("Status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    statusBat2 = dataSnapshot.getValue(String.class);
                    switch (statusBat2) {
                        case "Charging":
                                /* Off
                                source_line.setVisibility(View.INVISIBLE);
                                line_bat1.setVisibility(View.INVISIBLE);
                                line2_bat1.setVisibility(View.INVISIBLE);
                                line_bat1o.setVisibility(View.VISIBLE);
                                line2_bat1o.setVisibility(View.VISIBLE);

                                line_bat2.setVisibility(View.INVISIBLE);
                                line2_bat2.setVisibility(View.INVISIBLE);
                                line_bat2o.setVisibility(View.VISIBLE);
                                line2_bat2o.setVisibility(View.VISIBLE);

                                bolt1.setVisibility(View.INVISIBLE);
                                bolt2.setVisibility(View.INVISIBLE);
                                break;
                                 */
                            source_line.setVisibility(View.VISIBLE);
                            source_lineo.setVisibility(View.INVISIBLE);
                            line_bat1.setVisibility(View.INVISIBLE);
                            line2_bat1.setVisibility(View.INVISIBLE);
                            line_bat1o.setVisibility(View.VISIBLE);
                            line2_bat1o.setVisibility(View.VISIBLE);

                            line_bat2.setVisibility(View.VISIBLE);
                            line2_bat2.setVisibility(View.VISIBLE);
                            line_bat2o.setVisibility(View.INVISIBLE);
                            line2_bat2o.setVisibility(View.INVISIBLE);

                            bolt1.setVisibility(View.INVISIBLE);
                            bolt2.setVisibility(View.VISIBLE);

                            bat1_meter.setSecondaryProgress(0);
                            bat2_meter.setSecondaryProgress(maxCharge);
                            break;
                        case "Supplying":
                            supply_line.setVisibility(View.VISIBLE);
                            supply_lineo.setVisibility(View.INVISIBLE);
                            line3_bat1.setVisibility(View.INVISIBLE);
                            line4_bat1.setVisibility(View.INVISIBLE);
                            line3_bat1o.setVisibility(View.VISIBLE);
                            line4_bat1o.setVisibility(View.VISIBLE);

                            line3_bat2.setVisibility(View.VISIBLE);
                            line4_bat2.setVisibility(View.VISIBLE);
                            line3_bat2o.setVisibility(View.INVISIBLE);
                            line4_bat2o.setVisibility(View.INVISIBLE);
                            break;

                        case "Off":
                            source_line.setVisibility(View.INVISIBLE);
                            source_lineo.setVisibility(View.VISIBLE);

                            line_bat2.setVisibility(View.INVISIBLE);
                            line2_bat2.setVisibility(View.INVISIBLE);
                            line_bat2o.setVisibility(View.VISIBLE);
                            line2_bat2o.setVisibility(View.VISIBLE);
                            line3_bat2.setVisibility(View.INVISIBLE);
                            line4_bat2.setVisibility(View.INVISIBLE);
                            line3_bat2o.setVisibility(View.VISIBLE);
                            line4_bat2o.setVisibility(View.VISIBLE);

                            bolt2.setVisibility(View.INVISIBLE);
                            bat2_meter.setSecondaryProgress(0);
                            break;
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("check", "Failed to read value.", error.toException());
                }
            });

            // Read maximum charge limit on app startup
            myRef.child("Max Charge").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        maxCharge = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                        if(Objects.equals(statusBat1, "Charging")){
                            bat1_meter.setSecondaryProgress(maxCharge);
                            bat2_meter.setSecondaryProgress(0);
                        } else if (Objects.equals(statusBat2, "Charging")) {
                            bat1_meter.setSecondaryProgress(0);
                            bat2_meter.setSecondaryProgress(maxCharge);
                        }
                    }
                }
            });
            // Update maximum charge limit when value changes
            myRef.child("Max Charge").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    maxCharge = dataSnapshot.getValue(Integer.class);
                    if(Objects.equals(statusBat1, "Charging")){
                        bat1_meter.setSecondaryProgress(maxCharge);
                        bat2_meter.setSecondaryProgress(0);
                    } else if (Objects.equals(statusBat2, "Charging")) {
                        bat1_meter.setSecondaryProgress(0);
                        bat2_meter.setSecondaryProgress(maxCharge);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("check", "Failed to read value.", error.toException());
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putInt("Charge Decision", bat_charge);
        editor.putInt("Supply Decision", bat_supply);
        editor.putInt("MAXCHARGE", maxCharge);
        editor.putBoolean("OFF", turnOff);
        editor.apply();
    }

    // Ensure only navigation bar closes when pressing back instead of entire app
    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    // navigation menu selection logic
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_settings:
                openPrefs();
                break;
            case R.id.nav_graph:
                openGraph();
                break;
            case R.id.con_us:
                openContact();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
    // open Settings Dialog Box
    public void openPrefs(){
        PrefDialog prefDialog = new PrefDialog();
        prefDialog.show(getSupportFragmentManager(), "pref dialog");
    }
    // open Daily Generation Activity
    public void  openGraph(){
        Intent i = new Intent( MainActivity.this, GraphDialog.class);
        startActivity(i);
    }
    // open Contact Us Dialog Box
    public void openContact(){
        ContactDialog contactDialog = new ContactDialog();
        contactDialog.show(getSupportFragmentManager(), "contact dialog");
    }
    @Override
    public void applyPrefs(int maxChargeIn, Boolean turnOffBool, String newMode) {
        maxCharge = maxChargeIn;
        turnOff = turnOffBool;
        test.setValue(newMode);
    }

}