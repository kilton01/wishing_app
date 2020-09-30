package com.example.fly;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class signUpScreen extends AppCompatActivity {
    private EditText Dob,email,first,last,phone1;
    private ImageView cal,next;
    private Calendar c;
    private EditText password;
    private FirebaseAuth Auth;
    private DatabaseReference ref;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sign Up");
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");


        Dob = findViewById(R.id.date_of_birth);
        cal = findViewById(R.id.imgdate);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        first = findViewById(R.id.first_name);
        last = findViewById(R.id.last_name);
        phone1 = findViewById(R.id.phone1);
        next = findViewById(R.id.next_screen);
        progressBar = findViewById(R.id.progressBar2);


        c = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        Dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(signUpScreen.this, date,c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String firstname = first.getText().toString().trim();
                String lastname = last.getText().toString().trim();
                String date_of_birth = Dob.getText().toString().trim();
                String emailAd = email.getText().toString().trim();

                if (firstname.equals("")){
                    Toast.makeText(signUpScreen.this, "Input First Name", Toast.LENGTH_SHORT).show();
                }else if (lastname.equals("")){
                    Toast.makeText(signUpScreen.this, "Input Last Name", Toast.LENGTH_SHORT).show();
                }else if (date_of_birth.equals("")){
                    Toast.makeText(signUpScreen.this, "Select Date", Toast.LENGTH_SHORT).show();
                }else if (emailAd.equals("")){
                    Toast.makeText(signUpScreen.this, "Input Email", Toast.LENGTH_SHORT).show();
                }
                else{
                    signUp();
                }


            }
        });

    }

    private void updateLabel(){
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat,Locale.US);
        Dob.setText(sdf.format(c.getTime()));
    }

    private void signUp(){
        Auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String currentUser = Auth.getCurrentUser().getUid();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("First_Name",first.getText().toString());
                hashMap.put("Last_Name",last.getText().toString());
                hashMap.put("Date_Of_Birth",Dob.getText().toString());
                hashMap.put("Phone_Number",phone1.getText().toString());
                hashMap.put("Email",email.getText().toString());
                hashMap.put("Password",password.getText().toString());


                ref.child(currentUser).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(signUpScreen.this, "Sucess!! Logging in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(signUpScreen.this,Main2Activity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(signUpScreen.this);
                        alert.setMessage(e.getMessage());
                        alert.setTitle("Error Message");
                        alert.show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(signUpScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
