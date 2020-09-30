package com.example.fly;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;


public class add_person {
    private static DatabaseReference ref,ref1;
    private static FirebaseAuth auth;
    private static AlertDialog.Builder alert,alert1;
    private static AlertDialog dialog,dialog1;

    static String remainderTime;
    static int hour;
    static int minute;
    static long alarmTime;


    public static void showMessage(final Activity activity, final LinearLayout linearLayout)    {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_save_message,linearLayout, false);

        final EditText personName = view.findViewById(R.id.nom);
        final EditText personDOB = view.findViewById(R.id.dob);
        final EditText personNum = view.findViewById(R.id.pnum);
        final EditText message = view.findViewById(R.id.mess);
        final ImageView cal = view.findViewById(R.id.imgStart);
        final ProgressBar pgBar = view.findViewById(R.id.progressBar2);

        final ImageView time = view.findViewById(R.id.setTime);
        final EditText eTime = view.findViewById(R.id.time);

        Button butt = view.findViewById(R.id.butt);
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(auth.getCurrentUser().getUid());
        ref1 = FirebaseDatabase.getInstance().getReference().child("message ids").child("man");

        final Calendar c = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                personDOB.setText(sdf.format(c.getTime()));
            }
        };

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity, date,c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (personName.getText().toString().trim().equals("")){
                    Toast.makeText(activity, "Enter Person's Name", Toast.LENGTH_SHORT).show();
                }else if ((personDOB.getText().toString()).equals("")){
                    Toast.makeText(activity, "Enter Date of Birth", Toast.LENGTH_SHORT).show();
                }else if ((message.getText().toString()).equals("")){
                    Toast.makeText(activity, "Input message", Toast.LENGTH_SHORT).show();
                }else if(eTime.getText().toString().equals("")){
                    Toast.makeText(activity,"Set a reminder",Toast.LENGTH_SHORT).show();
                }else   {
                    pgBar.setVisibility(view.VISIBLE);
                    sendMessage(activity,personName,personDOB,personNum,message,pgBar,eTime);
                }
            }
        });
        alert = new AlertDialog.Builder(activity);

        alert.setView(view);
        dialog = alert.create();
        dialog.show();

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(activity);
                View view1 = layoutInflater.inflate(R.layout.layout_set_reminder,linearLayout,false);

                final TimePicker timePicker = view1.findViewById(R.id.currentTime);
                final Button setButton = view1.findViewById(R.id.setBtn);
                final Button cancelButton = view1.findViewById(R.id.cancel);
                int notificationId = 1;

                alert1 = new AlertDialog.Builder(activity);
                alert1.setView(view1);
                dialog1 = alert1.create();
                dialog1.show();

                setButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hour = timePicker.getCurrentHour();
                        minute = timePicker.getCurrentMinute();

                        remainderTime = hour + ":" + minute;
                        eTime.setText(remainderTime);


                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.HOUR_OF_DAY, hour);
                        startTime.set(Calendar.MINUTE, minute);
                        startTime.set(Calendar.SECOND,0);
                        alarmTime = startTime.getTimeInMillis();
                        Toast.makeText(activity, "Done!", Toast.LENGTH_SHORT).show();
                        dialog1.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1.dismiss();
                    }
                });

            }
        });
    }

    private static void sendMessage(final Activity activity, final EditText personName, final EditText personDOB, final EditText personNum, final EditText message, final ProgressBar progressBar, final EditText eTime) {
        final int random = new Random().nextInt(1000000000) + 0;

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num = Integer.parseInt(dataSnapshot.getValue().toString());

                HashMap<String,String > map = new HashMap<>();
                map.put("Name",personName.getText().toString());
                map.put("Date",personDOB.getText().toString());
                map.put("Phone Number", personNum.getText().toString());
                map.put("Message",message.getText().toString());
                map.put("Reminder_Time",eTime.getText().toString());

                ref.child(String.valueOf(num)).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

                num +=1;
                ref1.setValue(num);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
