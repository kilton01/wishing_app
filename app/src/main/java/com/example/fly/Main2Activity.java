package com.example.fly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CalendarView calendarView;
//    private Button logout;
    private AutoCompleteTextView search;
    private LinearLayout layout,llayout;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private List<HashMap<String, String>> list = new ArrayList<>();
    private int num = 0;
    private String Selecteddate,cd;
    private Date currentDate;
    private Date selectedDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("FlyWish");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        calendarView = findViewById(R.id.cal);
//        logout = findViewById(R.id.logout);


//        txt_date = findViewById(R.id.txtView);
        layout = findViewById(R.id.layout);
        llayout = findViewById(R.id.llayout);
        search = findViewById(R.id.findme);
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(auth.getCurrentUser().getUid());


        getCurrentDate();
        getMessages();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_person.showMessage(Main2Activity.this,layout);
            }
        });

        Selecteddate = cd;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month = month + 1;
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Selecteddate = month + "/" + day + "/" + year;
                try {
                    selectedDate = sdf.parse(Selecteddate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                llayout.removeAllViews();
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, String> hash = list.get(i);
                        String nom = hash.get("Name");
                        String date = hash.get("Date");
                        String message = hash.get("Message");
                        String time = hash.get("Reminder_Time");

                        cd = Selecteddate;
                        Log.d("time", "onSelectData: "+Selecteddate);
                        try {
                            if (sdf.parse(date).compareTo(sdf.parse(cd)) == 0){
                                Log.d("cool",nom);
                                Log.d("cool", date);
                                Log.d("cool", message);
                                Log.d("time",time);
                                show(nom,date,message,time);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent in = new Intent(Main2Activity.this,Main2Activity.class);
            startActivity(in);
            finish();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id == R.id.log_out){
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(Main2Activity.this,MainActivity.class);
            startActivity(i);
            finish();
            Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getMessages(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("hehe", dataSnapshot.toString());
                list.clear();
                llayout.removeAllViews();
//                for(DataSnapshot grandchild:dataSnapshot.getChildren()){
                for(DataSnapshot parent:dataSnapshot.getChildren()){
                    String name = parent.child("Name").getValue().toString();
                    String num = parent.child("Phone Number").getValue().toString();
                    String date = parent.child("Date").getValue().toString();
                    String message = parent.child("Message").getValue().toString();
                    String rTime = parent.child("Reminder_Time").getValue().toString();

                    HashMap<String,String> map = new HashMap<>();
                    map.put("Name",name);
                    map.put("Phone Number",num);
                    map.put("Date",date);
                    map.put("Message",message);
                    map.put("Reminder_Time",rTime);
                    list.add(map);
                    Toast.makeText(Main2Activity.this, "Data loaded successfully", Toast.LENGTH_SHORT).show();
//                    }
                }
                if(list.size()>0){
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String,String> hash = list.get(i);
                        String nom = hash.get("Name");
                        String date = hash.get("Date");
                        String message = hash.get("Message");
                        String time = hash.get("Reminder_Time");
                        String phone = hash.get("Phone Number");

                        getReminder(time,message,date,phone);

                        cd = Selecteddate;
//                        Log.d("time", "onSelectData: "+Selecteddate);
                        try {
                            if (sdf.parse(date).compareTo(sdf.parse(cd)) == 0){
                                Log.d("cool",nom);
                                Log.d("cool", date);
                                Log.d("cool", message);
                                Log.d("time",time);
                                show(nom,date,message,time);

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }else{
                    Toast.makeText(Main2Activity.this, "Empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Main2Activity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void show(String nom, String date, String Message, String time){
        LayoutInflater inflater = LayoutInflater.from(Main2Activity.this);
        final View view1 = inflater.inflate(R.layout.viewpage, llayout, false);
        final TextView name = view1.findViewById(R.id.txtname);
        final TextView dob = view1.findViewById(R.id.txtdob);
        final TextView mess = view1.findViewById(R.id.txtmess);
        final TextView notifyTime = view1.findViewById(R.id.txtrTime);
        final Button button = view1.findViewById(R.id.update);


        name.setText(nom);
        dob.setText(date);
        mess.setText(Message);
        notifyTime.setText(time);


        llayout.addView(view1);
    }
//
    public void getCurrentDate(){
        try {
            cd = sdf.format(new Date(calendarView.getDate()));
            Log.d("time", "getCurrentDate: "+cd);
            currentDate = sdf.parse(cd);
            Log.d("time_current", currentDate.toString());
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void getReminder(@org.jetbrains.annotations.NotNull String Time, String Message, @NotNull String Date, String phone){
        String[] parts = Time.split(":");
        String[] dateParts = Date.split("/");
        //date
        String year = dateParts[2];
        String month = dateParts[0];
        String day = dateParts[1];

        Log.d("date", month);
        Log.d("date", day);
        //time
        String Hour = parts[0];
        String Minutes = parts[1];

        Log.d("time",Hour);
        Log.d("time",Minutes);

        Intent intent = new Intent(Main2Activity.this, Notifier.class);
        intent.putExtra("message", Message);
        intent.putExtra("Phone Number", phone);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(Main2Activity.this,0,
                intent,PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        int hour = Integer.parseInt(Hour);
        int minute = Integer.parseInt(Minutes);

        Calendar startTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
//        startTime.set(Calendar.YEAR, 2019);
        startTime.set(Calendar.MONTH, Integer.parseInt(month)-1);
        startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND,0);
        long alarmStartTime = startTime.getTimeInMillis();

        alarm.set(AlarmManager.RTC_WAKEUP,alarmStartTime,alarmIntent);

        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();

    }

}

