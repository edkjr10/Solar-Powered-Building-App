package com.example.solaris;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.solaris.databinding.ActivityMainBinding;

public class ContactDialog extends AppCompatDialogFragment {
    View view;
    Button email, call;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // set view to Contact Us dialog
        view = LayoutInflater.from(getActivity()).inflate(R.layout.contact_dialog, null);
        email = view.findViewById(R.id.email_button);
        call = view.findViewById(R.id.phone_button);
        String recipientList = "edkjr10@tamu.edu";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fill Recipient line with my email address
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipient = recipientList.split(",");
                intent.putExtra(Intent.EXTRA_EMAIL, recipient);

                // Fill Subject line with customer support title
                intent.putExtra(Intent.EXTRA_SUBJECT, "Solaris Customer Support Request");
                // Allow user to specify preferred app for email
                intent.setType("message/rfc822");

                if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getContext(), "No Email Application Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // Propagate the phone number in the call app with my number
                intent.setData(Uri.parse("tel:4697268610"));
                startActivity(intent);
            }
        });
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
            }
        });
        return builder.create();
    }
}
