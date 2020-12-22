package com.example.wayup_mobile_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity{

    // variable for alert PopUp
    AlertDialog dialog;
    // variable for drawerMenu
    DrawerLayout drawerLayout;
    // variables for loading problem from Database
    GridLayout mainWall;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ArrayList<Problem> allProblems = new ArrayList<Problem>();
    int current_problem_index;
    String[] width = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"};
    String[] height = {"15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};

    // variables for establishing socket connection
    SequenceSender sequenceSender;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainWall = findViewById(R.id.gridlayout_mainscreen);
        // Fill ArrayList with problems from Database, so you can show them with load_problem
        loadDatabase();
        current_problem_index = 0;

        // TOP NAVIGATION CODE
        drawerLayout = findViewById(R.id.drawer_layout);

        // BOTTOM NAVIGATION CODE
        // Initialize bottomNavigation variable
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set default state
        bottomNavigationView.setSelectedItemId(R.id.show_problem);
        // set ActionListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    // load problem from Database
                    case R.id.load_problem:

                        Problem current_problem = allProblems.get(current_problem_index);
                        selectDatabaseProblem(current_problem.getSequence(), current_problem.getSequence_counters(), current_problem.getSequence_tags());

                        // check if the current_problem_index is still in range of array size - Avoid indexOutofBounds
                        if(current_problem_index < allProblems.size()-1){
                            current_problem_index ++; // increment the index to show new problem when load_problem is pressed again
                        }
                        // if the problem_index is equal to array size, reset it to zero - that way you start showing problems from the beginning again
                        else{
                            current_problem_index = 0;
                        }
                        return true;
                    // send the Problem sequence via WiFi to Arduino, which then lights the correct LEDs
                    case R.id.show_problem:

                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE); // get the WiFi status
                        // check if WiFi is enabled
                        if (wifiManager.isWifiEnabled()) {
                            // TODO: implement client socket communication

                            sendSequence(bottomNavigationView);

                        }
                        // if the WiFi is disable show AlertDialog to user
                        else{
                            final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                            builder.setTitle("WiFi ERROR");
                            builder.setMessage("Please connect to the WiFi");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog = builder.show();
                        }
                        return true;
                    // add new Problem to the Database, which redirects the user to SetProblemActivity
                    case R.id.add_problem:

                        startActivity(new Intent(getApplicationContext(), SetProblemActivity.class));
                        return true;
                }
                return false;
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        // check if you were redirected from DatabaseActivity. If the extra data in Intent is not empty, you show the Problem sequence on the wall
        if(getIntent().getStringExtra("mainScreen_sequence") != null){
            selectDatabaseProblem(getIntent().getStringExtra("mainScreen_sequence"), getIntent().getStringExtra("mainScreen_sequence_counters"), getIntent().getStringExtra("mainScreen_sequence_tags"));
        }
        // otherwise no data has been sent
        else{
            System.out.println("No data has been sent!");
        }

    }


    public void ClickMenu(View view){
        // open Drawer
        openDrawer(drawerLayout);
        
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        // open Drawer layout
        drawerLayout.openDrawer((GravityCompat.START));
    }
    
    public void ClickLogo(View view) {
        // close Drawer
        closeDrawer(drawerLayout);
        
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        // close Drawer layout
        //check current state of the Drawer
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            // close the Drawer
            drawerLayout.closeDrawer((GravityCompat.START));
        }
    }

    public void ClickHome(View view){
        recreate();
    }

    public void ClickDatabase(View view){
        redirectActivity(this, DatabaseActivity.class);
    }

    public static void redirectActivity(Activity activity, Class aClass) {
        // Initialize intent
        Intent intent = new Intent(activity, aClass);
        // Set Flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start the activity
        activity.startActivity(intent);
    }


    @Override
    protected void onPause(){
        super.onPause();
        // close Drawer
        closeDrawer(drawerLayout);

    }

    public void loadDatabase(){
        Cursor cursor = databaseHelper.getAllData();
        // check if database is empty
        if(cursor.getCount() == 0){
            Toast.makeText(getApplicationContext(),"ERROR: Empty Database." , Toast.LENGTH_LONG).show();
        }
        else{
            while(cursor.moveToNext()){
                Problem new_problem = new Problem(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3),
                        cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7));
                allProblems.add(new_problem);
            }
        }

    }

    public void selectDatabaseProblem(String sequence, String sequence_counters, String sequence_tags){

        clearWall(); // clear GridLayout

        String[] holds = sequence.split(",");
        String[] hold_counters = sequence_counters.split(",");
        String[] hold_tags = sequence_tags.split(",");

        for (int i = 0; i < holds.length; i ++){
            ImageView hold_image = (ImageView) mainWall.findViewById(Integer.parseInt(holds[i]));
            TextView hold_text = (TextView) mainWall.findViewById(Integer.parseInt(hold_counters[i]));
            hold_text.setText(String.valueOf(i +1));
            ShapeDrawable first = new ShapeDrawable(new OvalShape());
            first.setIntrinsicHeight(hold_image.getHeight() / 2);
            first.setIntrinsicWidth(hold_image.getHeight() / 2);
            // select the correct color for the hold background
            switch (hold_tags[i]){
                case "green":
                    first.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                            R.color.hold_green_background, null));
                    break;
                case "blue":
                    first.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                            R.color.hold_blue_background, null));
                    break;
                case "red":
                    first.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                            R.color.hold_red_background, null));
                    break;
            }
            // apply color to the hold background
            hold_image.setBackground(first);

        }


    }
    public void clearWall(){

        for(String width: width){
            for(String height: height){
                // get ID of the ImageView
                int hold_id = getResources().getIdentifier(width+height, "id", getPackageName());
                ImageView selected_hold = (ImageView) mainWall.findViewById(hold_id);

                int vHeight = selected_hold.getHeight();
                // set the background color of the ImageView
                ShapeDrawable empty = new ShapeDrawable(new OvalShape());
                empty.setIntrinsicHeight(vHeight / 2);
                empty.setIntrinsicWidth(vHeight / 2);
                empty.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorClimbingWall, null));;
                selected_hold.setBackground(empty);
                selected_hold.setTag("empty");

                // get ID of the TextView
                int hold_counter = getResources().getIdentifier("counter_"+width+height, "id", getPackageName());
                TextView selected_counter = (TextView) mainWall.findViewById(hold_counter);
                // set TextView text
                selected_counter.setText("");

            }
        }

    }

    public void sendSequence(View view){
        sequenceSender = new SequenceSender();
        sequenceSender.execute("Tole pa zdej dela");
    }

}
