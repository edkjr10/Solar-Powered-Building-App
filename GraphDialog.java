package com.example.solaris;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
/*

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
 */
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GraphDialog extends AppCompatActivity{
    View view;
    Button save, close;
    String graphChoice, month, day, weekDay, monthIntS, prevMonthS, yearS, dayCounterS;
    TextView chartTitle;

    int dayInt, startDay, dayTotal, monthInt, prevMonth, labelCount, year, labelStart, labelEnd, dayCounter, childPow, powToday, powYest, count;
    RadioGroup graphRadio;
    RadioButton monthRadio, weekRadio, todayRadio;
    GraphView graphView;
    BarGraphSeries<DataPoint> series;
    DataPoint[] values;
    EditText dummyVal;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.graph);


        //graph settings

        graphView = findViewById(R.id.idGraphView);
        chartTitle = findViewById(R.id.graphTitle);
        dummyVal = findViewById(R.id.graphDummy);

        // Get date information from current day
        DateFormat currentYear = new SimpleDateFormat("yy");
        DateFormat currentMonth = new SimpleDateFormat("MMMM");
        DateFormat currentMonthI = new SimpleDateFormat("MM");
        DateFormat currentDate = new SimpleDateFormat("dd");
        DateFormat currentWeekday = new SimpleDateFormat("EEEE");

        Date date = new Date();
        yearS = currentYear.format(date);
        year = Integer.parseInt(yearS);
        month = currentMonth.format(date);
        monthIntS = currentMonthI.format(date);
        day = currentDate.format(date);
        weekDay = currentWeekday.format(date);
        dayInt = Integer.parseInt(day);
        monthInt = Integer.parseInt(monthIntS);
        chartTitle.setText(month);

        prevMonth = monthInt - 1;

        //Logic to find date to start
        switch (prevMonth){
            case 1:
                prevMonthS = "January";
                startDay = dayInt + 2;
                dayTotal = 31;
                break;
            case 2:
                prevMonthS = "February";
                if (year % 4 == 0){
                    startDay = dayInt;
                    dayTotal = 29;
                }
                break;
            case 3:prevMonthS = "March";
                startDay = dayInt + 2;
                dayTotal = 31;
                break;
            case 4:
                prevMonthS = "April";
                startDay = dayInt + 1;
                dayTotal = 30;
                break;
            case 5:
                prevMonthS = "May";
                startDay = dayInt + 2;
                dayTotal = 31;
                break;
            case 6:
                prevMonthS = "June";
                startDay = dayInt + 1;
                dayTotal = 30;
                break;
            case 7:
                prevMonthS = "July";
                startDay = dayInt + 2;
                dayTotal = 31;
                break;
            case 8:
                prevMonthS = "August";
                startDay = dayInt + 2;
                dayTotal = 31;
                break;
            case 9:
                prevMonthS = "September";
                startDay = dayInt + 1;
                dayTotal = 30;
                break;
            case 10:
                prevMonthS = "October";
                startDay = dayInt + 2;
                dayTotal = 31;
                break;
            case 11:
                prevMonthS = "November";
                startDay = dayInt + 1;
                dayTotal = 30;
                break;
            case 12:
                prevMonthS = "December";
                startDay = dayInt + 2;
                dayTotal = 31;
                break;
        }

        // Connect to database and read from current month and previous month child branches
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference monthRef = database.getReference(month);
        DatabaseReference prevMonthRef = database.getReference(prevMonthS);
        DatabaseReference sysRef = database.getReference("test");

        Log.d("check", "onCreate: " + month + " " + dayInt);

        // Graph power generation for current day on dialog creation
            monthRef.child(Integer.toString(dayInt)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        powToday = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                        dummyVal.setText(String.valueOf(powToday));
                        Log.d("check", "onComplete1: " + powToday + " " + dummyVal.getText());
                    }
                }
            });
            dummyVal.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                            new DataPoint(0, 0),
                            new DataPoint(1, Integer.parseInt(String.valueOf(dummyVal.getText())))
                    });
                    graphView.addSeries(series);
                }
            });

        labelCount = 1;
        Log.d("check", "after OnComplete2: " + powToday);

        // Further graph settings
        graphView.setTitle(month + " " + day);
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Power Generated (W)");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Day");
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(2);



        //Radio Group
        graphRadio = findViewById(R.id.graphGroup);
        monthRadio = findViewById(R.id.radioMonth);
        weekRadio = findViewById(R.id.radioWeek);
        todayRadio = findViewById(R.id.radioToday);

        todayRadio.setChecked(true);

        // Listener for Graph Type Selection
        graphRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int j) {
                if(monthRadio.isChecked()){
                    graphChoice = "This Month";
                } else if (weekRadio.isChecked()) {
                    graphChoice = "This Week";
                } else if (todayRadio.isChecked()) {
                    graphChoice = "Today";
                }

                count = 0;
                labelCount = 1;
                switch(graphChoice) {
                    case "This Month":
                        // Reset graph with entries from the last 30 days
                        graphView.removeAllSeries();
                        values = new DataPoint[30];
                        // Find entries included in previous month
                        for (int i = 1; i < 31 - dayInt; i++) {
                            dayCounter = dayInt + i;
                            dayCounterS = String.valueOf(dayCounter);
                            Log.d("check", "getData: " + dayCounterS);
                            prevMonthRef.child(dayCounterS).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                    else {
                                        String tunnel = String.valueOf(task.getResult().getValue());
                                        //childPow = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                                        dummyVal.setText(tunnel);
                                        Log.d("check", "getData: " + prevMonthS + " " + childPow + " " + dummyVal.getText());
                                    }
                                }
                            });
                            if(labelCount == 1){
                                graphView.setTitle(prevMonthS + " " + dayCounter + " - " + month + " " + dayInt);
                            }

                            dummyVal.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    childPow = Integer.parseInt(String.valueOf(dummyVal.getText()));
                                    //values[count] = new DataPoint(labelCount, childPow);
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    childPow = Integer.parseInt(String.valueOf(dummyVal.getText()));
                                }
                            });
                            Log.d("check", "onCheckedChanged: " + labelCount + " " + childPow);
                            values[count] = new DataPoint(labelCount, childPow);
                            labelCount += 1;
                            count += 1;
                        }
                        // Find entries for current month
                        for (int i = 1; i < dayInt + 1; i++) {
                            dayCounterS = String.valueOf(i);
                            monthRef.child(dayCounterS).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                    else {
                                        String tunnel = String.valueOf(task.getResult().getValue());
                                        dummyVal.setText(tunnel);
                                        Log.d("check", "getData: " + prevMonthS + " " + childPow + " " + dayCounter);
                                    }
                                }
                            });
                            if(labelCount == 1){
                                graphView.setTitle(prevMonthS + " " + dayCounter + " - " + month + " " + dayInt);
                            }
                            dummyVal.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    childPow = Integer.parseInt(String.valueOf(dummyVal.getText()));
                                    //values[count] = new DataPoint(labelCount, childPow);
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    childPow = Integer.parseInt(String.valueOf(dummyVal.getText()));
                                }
                            });
                            values[count] = new DataPoint(labelCount, childPow);
                            labelCount += 1;
                            count += 1;
                        }
                        graphView.getViewport().setXAxisBoundsManual(true);
                        graphView.getViewport().setMinX(0.31);
                        graphView.getViewport().setMaxX(1.8);

                        break;
                    case "This Week":
                        // Reset graph with entries from last 7 days
                        graphView.removeAllSeries();
                        values = new DataPoint[7];
                        for (int i = 1; i < 8 - dayInt; i++) {
                            //for (int i = 1; i < 31; i++) {
                            dayCounter = dayInt + i;
                            dayCounterS = String.valueOf(dayCounter);
                            Log.d("check", "getData: " + dayCounterS);
                            prevMonthRef.child(dayCounterS).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                    else {
                                        String tunnel = String.valueOf(task.getResult().getValue());
                                        //childPow = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                                        dummyVal.setText(tunnel);
                                        Log.d("check", "getData: " + prevMonthS + " " + childPow + " " + dummyVal.getText());
                                    }
                                }
                            });
                            dummyVal.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    childPow = Integer.parseInt(String.valueOf(dummyVal.getText()));
                                    //values[count] = new DataPoint(labelCount, childPow);
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    childPow = Integer.parseInt(String.valueOf(dummyVal.getText()));
                                }
                            });

                            Log.d("check", "onCheckedChanged: " + labelCount + " " + childPow);
                            values[count] = new DataPoint(labelCount, childPow);
                            labelCount += 1;
                            count += 1;
                        }
                        for (int i = 1; i < 8; i++) {
                            dayCounter = dayInt - 7 + i;
                            dayCounterS = String.valueOf(dayCounter);
                            monthRef.child(dayCounterS).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                    else {
                                        String tunnel = String.valueOf(task.getResult().getValue());
                                        dummyVal.setText(tunnel);
                                        Log.d("check", "getData: " + prevMonthS + " " + childPow + " " + dayCounter);
                                    }
                                }
                            });
                            dummyVal.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    childPow = Integer.parseInt(String.valueOf(dummyVal.getText()));
                                    //values[count] = new DataPoint(labelCount, childPow);
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    childPow = Integer.parseInt(String.valueOf(dummyVal.getText()));
                                }
                            });
                            values[count] = new DataPoint(labelCount, childPow);
                            if(labelCount == 1){
                                graphView.setTitle(month + " " + dayCounter + " - " + month + " " + dayInt);
                            }

                            labelCount += 1;
                            count += 1;
                        }
                        graphView.getViewport().setXAxisBoundsManual(true);
                        graphView.getViewport().setMinX(0.6);
                        graphView.getViewport().setMaxX(1.5);
                        break;

                    case "Today":
                        // reset graph generation from current day
                        graphView.removeAllSeries();
                        monthRef.child(Integer.toString(dayInt)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    powToday = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                                    dummyVal.setText(String.valueOf(powToday));
                                    Log.d("check", "onComplete1: " + powToday + " " + dummyVal.getText());
                                }
                            }
                        });
                        dummyVal.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                                        new DataPoint(0, 0),
                                        new DataPoint(1, Integer.parseInt(String.valueOf(dummyVal.getText())))
                                });
                            }
                        });
                        graphView.setTitle(month + " " + day);
                        graphView.getViewport().setMinX(0);
                        graphView.getViewport().setMaxX(2);
                        break;
                }
                series.resetData(values);
            }
        });


        //Buttons
        save = findViewById(R.id.save_button);
        close = findViewById(R.id.close_button);

        // Save graph to a specified location
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                graphView.takeSnapshotAndShare(GraphDialog.this, "exampleGraph", "GraphViewSnapshot");
            }
        });

        // Close activity and return to overview page
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( GraphDialog.this, MainActivity.class);
                startActivity(i);
            }
        });

    }
}