package com.example.fly;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.NotificationChannel.DEFAULT_CHANNEL_ID;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    //    private DatabaseReference data;
    private Button login;
    private EditText userEntry, passEntry;
    private TextView forget_password, signUp;
//    private List<HashMap<String, String>> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("FlyWish");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //Initializing FirebaseAuth
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent i = new Intent(MainActivity.this, Main2Activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            Toast.makeText(this, "I work", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("login", "onAuthStateChanged: signed_out");
        }


        login = findViewById(R.id.login);
        userEntry = findViewById(R.id.Euser);
        passEntry = findViewById(R.id.pass);
        forget_password = findViewById(R.id.forget);
        signUp = findViewById(R.id.newAccess);

        //delete later
        userEntry.setText("realsteve22@gmail.com");
        passEntry.setText("darphen22");


        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth.signInWithEmailAndPassword(userEntry.getText().toString(), passEntry.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
//                        data = FirebaseDatabase.getInstance().getReference().child("Messages").child(mAuth.getCurrentUser().getUid());
//                        dataLoadOnStart(intent);
//                        Toast.makeText(MainActivity.this, "Data loaded successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        Snackbar.make(view, "Logging in ....", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }
                });


        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "forgotten password? You ain't serious", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, signUpScreen.class);
                startActivity(intent);
            }
        });


    }
}



//    public void dataLoadOnStart(final Intent intent){
//        data.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("hehe", dataSnapshot.toString());
//                list.clear();
//                for(DataSnapshot parent:dataSnapshot.getChildren()){
//                    String name = parent.child("Name").getValue().toString();
//                    String num = parent.child("Phone Number").getValue().toString();
//                    String date = parent.child("Date").getValue().toString();
//                    String message = parent.child("Message").getValue().toString();
//                    String rTime = parent.child("Reminder_Time").getValue().toString();
//
////                                    Log.d("llll",name);
//                    HashMap<String,String> map = new HashMap<>();
//                    map.put("Name",name);
//                    map.put("Phone Number",num);
//                    map.put("Date",date);
//                    map.put("Message",message);
//                    map.put("Reminder_Time",rTime);
//                    list.add(map);
//                }

//                if(list.size()>0){
//                    for (int i = 0; i < list.size(); i++) {
//                        HashMap<String,String> hash = list.get(i);
//                        intent.putExtra("Name",hash.get("Name"));
//                        intent.putExtra("Date",hash.get("Date"));
//                        intent.putExtra("Message",hash.get("Message"));
//                        intent.putExtra("Reminder_Time", hash.get("Reminder_Time"));
//                    }

//                }else{
//                    Toast.makeText(MainActivity.this, "Empty", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }


//    public void saving(){
//        FileOutputStream fos = null;
//        try {
//            fos = openFileOutput(fileName, MODE_PRIVATE);
//            fos.write();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }
//}



