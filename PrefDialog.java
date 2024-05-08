package com.example.solaris;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SwitchCompat;
import android.app.Activity;
import android.app.Dialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class PrefDialog extends AppCompatDialogFragment{
    RelativeLayout relativeLayout;
    View view;
    TextView offText;
    SwitchCompat switchCompat;
    EditText maxChargeIn;
    String maxChargeStr, mode, statusSys;
    int maxCharge;
    public Boolean turnOff;
    PrefListener listener;
    public SharedPreferences sharedPrefs;
    RadioGroup modePow;
    RadioButton modeNorm, modeMax, modeCustom;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set view to the Setting Dialog interface
        view = LayoutInflater.from(getActivity()).inflate(R.layout.pref_dialog, null);

        // declare relevant UI elements
        offText = view.findViewById(R.id.pref_off);
        switchCompat = view.findViewById(R.id.switch1);
        maxChargeIn = view.findViewById(R.id.maxCharge);
        modePow = view.findViewById(R.id.modeGroup);
        modeNorm = view.findViewById(R.id.radioNorm);
        modeMax = view.findViewById(R.id.radioMaxPow);
        modeCustom = view.findViewById(R.id.radioCustom);

        sharedPrefs = getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        // Connect to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setView(view);


        turnOff = sharedPrefs.getBoolean("OFFPR", false);
        mode = sharedPrefs.getString("MODE", "Normal");

        DatabaseReference prefRef = database.getReference("System");

        // Read system status on dialog creation
        prefRef.child("Status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    statusSys = String.valueOf(task.getResult().getValue());
                    if(statusSys.equals("Off")){
                        switchCompat.setChecked(true);
                        switchCompat.setTextColor(Color.BLACK);
                        offText.setTextColor(Color.WHITE);
                        maxChargeIn.setEnabled(false);
                        maxChargeIn.setTextColor(Color.GRAY);
                    } else if (statusSys.equals("On"))
                    {
                        switchCompat.setChecked(false);
                        switchCompat.setTextColor(Color.WHITE);
                        offText.setTextColor(Color.BLACK);
                        maxChargeIn.setEnabled(true);
                        maxChargeIn.setTextColor(Color.WHITE);
                    }
                }
            }
        });

        // Off switch logic
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switchCompat.isChecked()) {
                    switchCompat.setTextColor(Color.BLACK);
                    offText.setTextColor(Color.WHITE);
                    maxChargeIn.setEnabled(false);
                    maxChargeIn.setTextColor(Color.GRAY);
                }
                else{
                    switchCompat.setTextColor(Color.WHITE);
                    offText.setTextColor(Color.BLACK);
                    maxChargeIn.setEnabled(true);
                    maxChargeIn.setTextColor(Color.WHITE);
                }
            }
        });

        // Read maximum charge limit on dialog creation
        prefRef.child("Max Charge").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    maxChargeStr = String.valueOf(task.getResult().getValue());
                    maxChargeIn.setText(maxChargeStr);
                }
            }
        });
        maxChargeStr = maxChargeIn.getText().toString();
        Log.d("Output", maxChargeStr);
        //Listener for EditText
        maxChargeIn.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                maxChargeStr = maxChargeIn.getText().toString();
                Log.d("Output", maxChargeStr);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
                maxChargeStr = maxChargeIn.getText().toString();
                Log.d("Output", maxChargeStr);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        // Read current charge mode on dialog startup
        prefRef.child("Mode").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    mode = String.valueOf(task.getResult().getValue());
                    if (Objects.equals(mode, "Normal")){
                        modeNorm.setChecked(true);
                        maxChargeIn.setText("85");
                        maxChargeIn.setEnabled(false);
                        maxChargeIn.setTextColor(Color.GRAY);
                    } else if (Objects.equals(mode, "Max Power")) {
                        modeMax.setChecked(true);
                        maxChargeIn.setText("100");
                        maxChargeIn.setEnabled(false);
                        maxChargeIn.setTextColor(Color.GRAY);
                    }
                    else if (Objects.equals(mode, "Custom")) {
                        modeCustom.setChecked(true);
                        maxChargeIn.setEnabled(true);
                        maxChargeIn.setTextColor(Color.WHITE);
                    }
                }
            }
        });

        // Listener for Charge Mode Radio Group
        modePow.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(modeNorm.isChecked()){
                    mode = "Normal";
                    maxChargeIn.setText("85");
                    maxChargeIn.setEnabled(false);
                    maxChargeIn.setTextColor(Color.GRAY);
                } else if (modeMax.isChecked()) {
                    mode = "Max Power";
                    maxChargeIn.setText("100");
                    maxChargeIn.setEnabled(false);
                    maxChargeIn.setTextColor(Color.GRAY);
                } else if (modeCustom.isChecked()){
                    mode = "Custom";
                    maxChargeIn.setEnabled(true);
                    maxChargeIn.setTextColor(Color.WHITE);
                }
            }
        });

        // Close dialog when Cancel is pressed
        builder1.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Write new system preferences to databse for MCU
        builder1.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (switchCompat.isChecked()) {
                            // Warning dialog box if Off switch is selected
                            AlertDialog.Builder builder_save = new AlertDialog.Builder(getActivity());
                            builder_save.setMessage("Batteries will not be charged or supply power if OFF is selected.\n\n" +
                                    "Confirm shutting down the system.");
                            builder_save.setCancelable(true);
                            // Write preferences if confirmed
                            builder_save.setPositiveButton(
                                    "Confirm",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            turnOff = true;
                                            prefRef.child("Status").setValue("Off");
                                            prefRef.child("Battery 1").child("Status").setValue("Off");
                                            prefRef.child("Battery 2").child("Status").setValue("Off");
                                            dialog.cancel();
                                        }
                                    });
                            // Close dialog if canceled
                            builder_save.setNegativeButton(
                                    "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert11 = builder_save.create();
                            alert11.show();
                        }
                        else{
                            turnOff = false;
                            prefRef.child("Status").setValue("On");
                            maxCharge = Integer.valueOf(maxChargeStr);
                        }
                    listener.applyPrefs(maxCharge, turnOff, mode);
                        prefRef.child("Max Charge").setValue(maxCharge);
                        prefRef.child("Mode").setValue(mode);
                    }
                });
        return builder1.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("MAXCHARGE", maxCharge);
        editor.putBoolean("OFFPR", turnOff);
        editor.putString("MODE", mode);
        editor.apply();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (PrefListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement pref listener");
        }
    }

    public interface PrefListener{
        void applyPrefs(int maxCharge, Boolean turnoff, String newMode);
    }

}
