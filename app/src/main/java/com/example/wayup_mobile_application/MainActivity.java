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
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    // variable for alert PopUp
    AlertDialog dialog;
    // variable for drawerMenu
    DrawerLayout drawerLayout;
    // variables for loading problem from Database
    GridLayout mainWall; //Variable for main climbing wall
    DatabaseHelper databaseHelper = new DatabaseHelper(this); // Initialize the instance of DatabaseHelper for loading problems
    ArrayList<Problem> allProblems = new ArrayList<>(); // ArrayList for holding the problems loaded from Database
    int current_problem_index; // index of currently displayed problem from Database
    String[] width = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
    String[] height = {"16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
    HashMap<String,String> current_sequence_data = new HashMap<>(); // global variable for holding sequence information
    HashMap<String,String> second_sequence_data = new HashMap<>(); // global variable for holding second sequence information
    HashMap<String,Integer> hold_led_numbers = new HashMap<>(); // Array that maps the hold_id to actual LED number that should be turned on
    TextView selected_problem_info; // TextView above the climbing wall. It is used to display problem name and grade
    boolean primary_problem_color = true; // color to used to display the problem. This is used in case that there are displayed two problems. True indicates that primary color should be used!
    boolean loaded_from_library = false; // variable that tells us, if the current problem on the wall is from Problem Library
    SwitchMaterial two_problem_option; // Switch in the main_drawer that allows user to display two problems at once
    // ProblemViewModel mViewModel;  viewModel which can be used for keeping score of some values that change - currently not used

    // variable for turning off lights when application is closed
    boolean opened = true;

    // VARIABLES for establishing socket connection
    FirstSequenceSender firstSequenceSender;
    SecondSequenceSender secondSequenceSender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //current_sequence_data.put("Sequence","The dictionary is empty");
        // assign default values to the HashMap
        mainWall = findViewById(R.id.gridlayout_mainscreen);
        // fill the HasHMap with hold led numbers. This is used then to send numbers of LED diodes that need to be lit up to Arduino
        hold_led_numbers.put("A", 0);
        hold_led_numbers.put("B", 15);
        hold_led_numbers.put("C", 30);
        hold_led_numbers.put("D", 45);
        hold_led_numbers.put("E", 60);
        hold_led_numbers.put("F", 75);
        hold_led_numbers.put("G", 90);
        hold_led_numbers.put("H", 105);
        hold_led_numbers.put("I", 120);
        hold_led_numbers.put("J", 135);
        hold_led_numbers.put("K", 150);

        // Fill ArrayList with problems from Database, so you can show them with load_problem
        loadFromDatabase();
        current_problem_index = 0;

        // TOP NAVIGATION CODE
        drawerLayout = findViewById(R.id.drawer_layout);
        // Initialize problem textView, which holds the name and grade
        selected_problem_info = findViewById(R.id.selected_problem_info);
        // Initialize problem Switch in the drawer, that allows user to display two problems at once
        two_problem_option = findViewById(R.id.two_problems_switch);
        two_problem_option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // check if the switch has been turned off and switch back to displaying one problem
                if(!two_problem_option.isChecked() && !second_sequence_data.isEmpty()){
                    clearSpecificProblem(current_sequence_data.get("Sequence"), current_sequence_data.get("Sequence_counters"), current_sequence_data.get("Sequence_tags"));
                    // check if HashMap is not empty
                    if(!second_sequence_data.isEmpty()){
                        // replace the values of the current_sequence_data with values from the second_sequence_data
                        current_sequence_data.put("Sequence",second_sequence_data.get("Sequence"));// add problem sequence to the HashMap as the currently selected problem
                        current_sequence_data.put("Sequence_counters",second_sequence_data.get("Sequence_counters"));// add problem sequence_counters to the HashMap as the currently selected problem
                        current_sequence_data.put("Sequence_tags",second_sequence_data.get("Sequence_tags"));// add problem sequence_tags to the HashMap as the currently selected problem
                        current_sequence_data.put("Sequence_name",second_sequence_data.get("Sequence_name"));// add problem sequence_name to the HashMap as the currently selected problem
                        current_sequence_data.put("Sequence_grade",second_sequence_data.get("Sequence_grade"));// add problem sequence_grade to the HashMap as the currently selected problem
                        current_sequence_data.put("Sequence_setter",second_sequence_data.get("Sequence_setter"));// add problem sequence_setter to the HashMap as the currently selected problem
                        current_sequence_data.put("Sequence_comment",second_sequence_data.get("Sequence_comment"));// add problem sequence_comment to the HashMap as the currently selected problem
                        // set the name and the grade of the problem
                        selected_problem_info.setText(getString(R.string.selected_problem_info, current_sequence_data.get("Sequence_name"), current_sequence_data.get("Sequence_grade")));
                        // reset the second_sequence HashMap
                        second_sequence_data = new HashMap<>();
                        // check which color was last used and keep the same color, when switch is turned off
                        clearWall();
                        showDatabaseProblem(current_sequence_data.get("Sequence"), current_sequence_data.get("Sequence_counters"), current_sequence_data.get("Sequence_tags")); // call function for displaying the problem on the graphic wall
                    }
                    // set the primary color back to true
                    primary_problem_color = true;
                }

                closeDrawer(drawerLayout);
            }
        });

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
                        if(allProblems.isEmpty()){
                            final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                            builder.setTitle("Try Again!");
                            builder.setMessage("There are no problems in the Library. Please add some.");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog = builder.show();
                        }
                        else{
                            // check if there is a problem displayed and check if switch is checked
                            if(!current_sequence_data.isEmpty() & two_problem_option.isChecked()){
                                Problem current_problem = allProblems.get(current_problem_index);
                                if(!second_sequence_data.isEmpty()){
                                    // Clear the previously displayed problem
                                    clearSpecificProblem(current_sequence_data.get("Sequence"), current_sequence_data.get("Sequence_counters"), current_sequence_data.get("Sequence_tags"));
                                    // replace the values for the current problem and with the second_sequence_data
                                    current_sequence_data.put("Sequence",second_sequence_data.get("Sequence"));// add problem sequence to the HashMap as the currently selected problem
                                    current_sequence_data.put("Sequence_counters",second_sequence_data.get("Sequence_counters"));// add problem sequence_counters to the HashMap as the currently selected problem
                                    current_sequence_data.put("Sequence_tags",second_sequence_data.get("Sequence_tags"));// add problem sequence_tags to the HashMap as the currently selected problem
                                    current_sequence_data.put("Sequence_name",second_sequence_data.get("Sequence_name"));// add problem sequence_name to the HashMap as the currently selected problem
                                    current_sequence_data.put("Sequence_grade",second_sequence_data.get("Sequence_grade"));// add problem sequence_grade to the HashMap as the currently selected problem
                                    current_sequence_data.put("Sequence_setter",second_sequence_data.get("Sequence_setter"));// add problem sequence_setter to the HashMap as the currently selected problem
                                    current_sequence_data.put("Sequence_comment",second_sequence_data.get("Sequence_comment"));// add problem sequence_comment to the HashMap as the currently selected problem

                                }
                                // prepare other data to show the second_sequence of the problem on the climbing wall
                                second_sequence_data.put("Sequence",current_problem.getSequence());// add problem sequence to the HashMap as the currently selected problem
                                second_sequence_data.put("Sequence_counters",current_problem.getSequence_counters());// add problem sequence_counters to the HashMap as the currently selected problem
                                second_sequence_data.put("Sequence_tags",current_problem.getSequence_tags());// add problem sequence_tags to the HashMap as the currently selected problem
                                second_sequence_data.put("Sequence_name",current_problem.getName());// add problem sequence_name to the HashMap as the currently selected problem
                                second_sequence_data.put("Sequence_grade",current_problem.getGrade());// add problem sequence_grade to the HashMap as the currently selected problem
                                second_sequence_data.put("Sequence_setter",current_problem.getSetter());// add problem sequence_setter to the HashMap as the currently selected problem
                                second_sequence_data.put("Sequence_comment",current_problem.getComment());// add problem sequence_comment to the HashMap as the currently selected problem
                                // set the name and the grade of the problem
                                selected_problem_info.setText(getString(R.string.two_selected_problems_info, current_sequence_data.get("Sequence_name"), current_sequence_data.get("Sequence_grade"),
                                        second_sequence_data.get("Sequence_name"), second_sequence_data.get("Sequence_grade")));
                                // use the secondary colors for displaying the problem
                                if(primary_problem_color){
                                    showSecondDatabaseProblem(current_problem.getSequence(), current_problem.getSequence_counters(), current_problem.getSequence_tags()); // call function for displaying the problem on the graphic wall
                                }
                                // use the primary colors for displaying the problem
                                if(!primary_problem_color){
                                    showDatabaseProblem(current_problem.getSequence(), current_problem.getSequence_counters(), current_problem.getSequence_tags()); // call function for displaying the problem on the graphic wall
                                }

                                // check if the current_problem_index is still in range of array size - Avoid indexOutofBounds
                                if(current_problem_index < allProblems.size()-1){
                                    current_problem_index ++; // increment the index to show new problem when load_problem is pressed again
                                }
                                // if the problem_index is equal to array size, reset it to zero - that way you start showing problems from the beginning again
                                else{
                                    current_problem_index = 0;
                                }
                                primary_problem_color = !primary_problem_color; // switch the value of primary color so the same color is not used for two problems
                            }
                            else{

                                /*check if the current problem displayed on the wall was loaded from the library.
                                If it was loaded, reset the climbing wall in order to correctly display next problems via button load problem
                                 */
                                if(loaded_from_library){
                                    // reset the wall and set loaded from library to false
                                    ResetWall(getCurrentFocus());
                                    loaded_from_library = false;
                                }
                                // get the instance of the current problem from the Database
                                Problem current_problem = allProblems.get(current_problem_index);
                                // set the name and the grade of the problem
                                selected_problem_info.setText(getString(R.string.selected_problem_info, current_problem.getName(), current_problem.getGrade()));
                                // prepare other data to show the sequence of the problem on the climbing wall
                                current_sequence_data.put("Sequence",current_problem.getSequence());// add problem sequence to the HashMap as the currently selected problem
                                current_sequence_data.put("Sequence_counters",current_problem.getSequence_counters());// add problem sequence_counters to the HashMap as the currently selected problem
                                current_sequence_data.put("Sequence_tags",current_problem.getSequence_tags());// add problem sequence_tags to the HashMap as the currently selected problem
                                current_sequence_data.put("Sequence_name",current_problem.getName());// add problem sequence_name to the HashMap as the currently selected problem
                                current_sequence_data.put("Sequence_grade",current_problem.getGrade());// add problem sequence_grade to the HashMap as the currently selected problem
                                current_sequence_data.put("Sequence_setter",current_problem.getSetter());// add problem sequence_setter to the HashMap as the currently selected problem
                                current_sequence_data.put("Sequence_comment",current_problem.getComment());// add problem sequence_comment to the HashMap as the currently selected problem
                                showDatabaseProblem(current_problem.getSequence(), current_problem.getSequence_counters(), current_problem.getSequence_tags()); // call function for displaying the problem on the graphic wall

                                // check if the current_problem_index is still in range of array size - Avoid indexOutofBounds
                                if(current_problem_index < allProblems.size()-1){
                                    current_problem_index ++; // increment the index to show new problem when load_problem is pressed again
                                }
                                // if the problem_index is equal to array size, reset it to zero - that way you start showing problems from the beginning again
                                else{
                                    current_problem_index = 0;
                                }
                            }

                        }
                        return true;
                    // send the Problem sequence via WiFi to Arduino, which then lights the correct LEDs
                    case R.id.show_problem:

                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE); // get the WiFi status
                        // check if WiFi is enabled
                        if (wifiManager.isWifiEnabled()) {

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
                        Intent intent = new Intent(getApplicationContext(), SetProblemActivity.class);
                        // Set Flag
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // send current_problem_data to SetProblemActivity for maintaining mainScreen UI
                        intent.putExtra("setScreen_sequence", current_sequence_data.get("Sequence"));
                        intent.putExtra("setScreen_sequence_tags", current_sequence_data.get("Sequence_tags"));
                        intent.putExtra("setScreen_sequence_counters", current_sequence_data.get("Sequence_counters"));
                        intent.putExtra("setScreen_sequence_name", current_sequence_data.get("Sequence_name"));
                        intent.putExtra("setScreen_sequence_grade", current_sequence_data.get("Sequence_grade"));
                        intent.putExtra("setScreen_sequence_setter", current_sequence_data.get("Sequence_setter"));
                        intent.putExtra("setScreen_sequence_comment", current_sequence_data.get("Sequence_comment"));
                        // Start the activity
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });


    }


    // TODO: Implement system for shutting down the lights, when the app is closed
    /*
    @Override
    public void onDestroy()
    {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE); // get the WiFi status
        // check if WiFi is enabled
        if (wifiManager.isWifiEnabled()) {
            opened = false; // set the variable for shutting down the lights
            sendSequence(getCurrentFocus());
            System.out.println("Pride!");

        }
        super.onDestroy();
    }
    */

    @Override
    protected void onStart() {
        super.onStart();

        /* check if you were redirected from DatabaseActivity or AddProblemActivity.
        If the extra data in Intent is not empty, you show the Problem sequence on the wall
        */

        // FIRSTLY check if there were two problems sent
        if(getIntent().getStringExtra("mainScreen_second_sequence") != null){

            //DISPLAY THE FIRST PROBLEM
            current_sequence_data.put("Sequence",getIntent().getStringExtra("mainScreen_sequence"));// add problem sequence to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_counters",getIntent().getStringExtra("mainScreen_sequence_counters"));// add problem sequence_counters to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_tags",getIntent().getStringExtra("mainScreen_sequence_tags"));// add problem sequence_tags to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_name",getIntent().getStringExtra("mainScreen_sequence_name"));// add problem sequence_name to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_grade",getIntent().getStringExtra("mainScreen_sequence_grade"));// add problem sequence_grade to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_setter",getIntent().getStringExtra("mainScreen_sequence_setter"));// add problem sequence_setter to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_comment",getIntent().getStringExtra("mainScreen_sequence_comment"));// add problem sequence_comment to the HashMap as the currently selected problem
            // call function for displaying the problem on the graphic wall
            showDatabaseProblem(getIntent().getStringExtra("mainScreen_sequence"), getIntent().getStringExtra("mainScreen_sequence_counters"), getIntent().getStringExtra("mainScreen_sequence_tags"));

            //DISPLAY THE SECOND PROBLEM
            second_sequence_data.put("Sequence",getIntent().getStringExtra("mainScreen_second_sequence"));// add problem sequence to the HashMap as the currently selected problem
            second_sequence_data.put("Sequence_counters",getIntent().getStringExtra("mainScreen_second_sequence_counters"));// add problem sequence_counters to the HashMap as the currently selected problem
            second_sequence_data.put("Sequence_tags",getIntent().getStringExtra("mainScreen_second_sequence_tags"));// add problem sequence_tags to the HashMap as the currently selected problem
            second_sequence_data.put("Sequence_name",getIntent().getStringExtra("mainScreen_second_sequence_name"));// add problem sequence_name to the HashMap as the currently selected problem
            second_sequence_data.put("Sequence_grade",getIntent().getStringExtra("mainScreen_second_sequence_grade"));// add problem sequence_grade to the HashMap as the currently selected problem
            second_sequence_data.put("Sequence_setter",getIntent().getStringExtra("mainScreen_second_sequence_setter"));// add problem sequence_setter to the HashMap as the currently selected problem
            second_sequence_data.put("Sequence_comment",getIntent().getStringExtra("mainScreen_second_sequence_comment"));// add problem sequence_comment to the HashMap as the currently selected problem
            // call function for displaying the problem on the graphic wall
            showSecondDatabaseProblem(getIntent().getStringExtra("mainScreen_second_sequence"), getIntent().getStringExtra("mainScreen_second_sequence_counters"), getIntent().getStringExtra("mainScreen_second_sequence_tags"));

            // set the name and the grade of the both problems
            selected_problem_info.setText(getString(R.string.two_selected_problems_info, current_sequence_data.get("Sequence_name"), current_sequence_data.get("Sequence_grade"),
                    second_sequence_data.get("Sequence_name"), second_sequence_data.get("Sequence_grade")));
            loaded_from_library = true; // set the variable for checking if problem was loaded from Database to true
        }
        // SECONDLY check if there was only one problem sent
        else if(getIntent().getStringExtra("mainScreen_sequence") != null){
            current_sequence_data.put("Sequence",getIntent().getStringExtra("mainScreen_sequence"));// add problem sequence to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_counters",getIntent().getStringExtra("mainScreen_sequence_counters"));// add problem sequence_counters to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_tags",getIntent().getStringExtra("mainScreen_sequence_tags"));// add problem sequence_tags to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_name",getIntent().getStringExtra("mainScreen_sequence_name"));// add problem sequence_name to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_grade",getIntent().getStringExtra("mainScreen_sequence_grade"));// add problem sequence_grade to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_setter",getIntent().getStringExtra("mainScreen_sequence_setter"));// add problem sequence_setter to the HashMap as the currently selected problem
            current_sequence_data.put("Sequence_comment",getIntent().getStringExtra("mainScreen_sequence_comment"));// add problem sequence_comment to the HashMap as the currently selected problem
            // set the name and the grade of the problem
            selected_problem_info.setText(getString(R.string.selected_problem_info, current_sequence_data.get("Sequence_name"), current_sequence_data.get("Sequence_grade")));
            primary_problem_color = false;
            // call function for displaying the problem on the graphic wall
            showDatabaseProblem(getIntent().getStringExtra("mainScreen_sequence"), getIntent().getStringExtra("mainScreen_sequence_counters"), getIntent().getStringExtra("mainScreen_sequence_tags"));
            loaded_from_library = true; // set the variable for checking if problem was loaded from Database to true
        }
        // otherwise no data has been sent
        else{
            System.out.println("No data has been sent!");
        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        // close Drawer
        closeDrawer(drawerLayout);

    }
    /*
    Function for displaying more information about the currently displayed problem. So far that information is: Setter and Comment
     */
    public void MoreInfo(View view){
        // show more_info alert dialog if problem is displayed
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        if(!current_sequence_data.isEmpty()){
            builder.setTitle("More info about the problem");
            if(!second_sequence_data.isEmpty()){
                builder.setMessage(getString(R.string.two_problems_selected_more_info, current_sequence_data.get("Sequence_name"), current_sequence_data.get("Sequence_setter"),
                        current_sequence_data.get("Sequence_comment"), second_sequence_data.get("Sequence_name"), second_sequence_data.get("Sequence_setter"), second_sequence_data.get("Sequence_comment")));
            }
            else{
                builder.setMessage(getString(R.string.selected_problem_more_info, current_sequence_data.get("Sequence_setter"), current_sequence_data.get("Sequence_comment")));
            }
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        // if problem is not displayed show error alertDialog
        else{
            builder.setTitle("Try Again!");
            builder.setMessage("It seems that there is no problem selected.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        dialog = builder.show();
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
        closeDrawer(drawerLayout);
    }

    public void ClickDatabase(View view){
        redirectActivity(this, DatabaseActivity.class);
    }

    /*
    helper function for managing redirection between activities
     */
    public static void redirectActivity(Activity activity, Class aClass) {
        // Initialize intent
        Intent intent = new Intent(activity, aClass);
        // Set Flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start the activity
        activity.startActivity(intent);
    }

    /*
    Function that completely resets the climbing wall (all the ImageViews and TextViews)
    and all other variables associated with displaying the problems
     */
    public void ResetWall(View view){
        clearWall(); // clear the climbing wall
        selected_problem_info.setText(""); // reset selected problem name and grade
        current_sequence_data = new HashMap<>(); // reset the dictionary for the selected problem
        second_sequence_data = new HashMap<>(); // reset the dictionary for the second selected problem
        current_problem_index = 0; // set the problem index to default value
        two_problem_option.setChecked(false); // set the switch to OFF mode
        closeDrawer(drawerLayout); // close the drawer
        // reset the lights on the real climbing wall
        firstSequenceSender = new FirstSequenceSender(getApplicationContext());
        firstSequenceSender.execute("reset_lights"); // string to send, that tells the controller to turn the lights off
    }


    /*
    Function that is called for loading data from the Database
     */
    public void loadFromDatabase(){
        Cursor cursor = databaseHelper.getAllData();
        // check if there are any entries in the Database
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

    /*
    Function to display primary sequence on the climbing wall
     */
    public void showDatabaseProblem(String sequence, String sequence_counters, String sequence_tags){

        /*check if the option for displaying two problems is switched off.
        Therefore you can clear the previous problem, before displaying the next one
        */
        if(second_sequence_data.isEmpty()){
            clearWall(); // clear GridLayout
        }

        String[] holds = sequence.split(",");
        String[] hold_counters = sequence_counters.split(",");
        String[] hold_tags = sequence_tags.split(",");

        for (int i = 0; i < holds.length; i ++){
            ImageView hold_image = (ImageView) mainWall.findViewById(Integer.parseInt(holds[i]));
            TextView hold_text = (TextView) mainWall.findViewById(Integer.parseInt(hold_counters[i]));
            // select the correct color for the hold background
            switch (hold_tags[i]){
                case "green":
                    // check if the current hold is shared by the already displayed sequence
                    if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_yellow, null).getConstantState())){
                        hold_image.setBackgroundResource(R.drawable.hold_background_overlap_start);
                        // get the currently displayed TextView text as array. This way you can update the dual counter correctly
                        String[] dual_counter = hold_text.getText().toString().split(" ");
                        // get the last part of the array, because that is the counter of previous problem
                        String previous_counter = dual_counter[dual_counter.length-1];
                        hold_text.setText(getString(R.string.two_selected_problems_counter, previous_counter, String.valueOf(i +1)));
                    }
                    // check if the current hold is already being shared and just change the counter text
                    else if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_overlap_start, null).getConstantState())){
                        hold_text.setText(String.valueOf(i +1));
                    }
                    // if the hold has not been displayed yet, set the color and counter text
                    else{
                        hold_image.setBackgroundResource(R.drawable.hold_background_green);
                        hold_text.setText(String.valueOf(i +1));
                    }

                    break;
                case "blue":
                    // check if current hold is shared by the already displayed sequence
                    if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_cyan, null).getConstantState())){
                        hold_image.setBackgroundResource(R.drawable.hold_background_overlap_intermediate);
                        // get the currently displayed TextView text as array. This way you can update the dual counter correctly
                        String[] dual_counter = hold_text.getText().toString().split(" ");
                        // get the last part of the array, because that is the counter of previous problem
                        String previous_counter = dual_counter[dual_counter.length-1];
                        hold_text.setText(getString(R.string.two_selected_problems_counter, previous_counter, String.valueOf(i +1)));
                    }
                    // check if the current hold is already being shared and just change the counter text
                    else if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(),R.drawable.hold_background_overlap_intermediate, null).getConstantState())){
                        hold_text.setText(String.valueOf(i +1));
                    }
                    // if the hold has not been displayed yet, set the color and counter text
                    else{
                        hold_image.setBackgroundResource(R.drawable.hold_background_blue);
                        hold_text.setText(String.valueOf(i +1));
                    }

                    break;
                case "red":
                    // check if current hold is shared by the already displayed sequence
                    if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(),R.drawable.hold_background_magenta, null).getConstantState())){
                        hold_image.setBackgroundResource(R.drawable.hold_background_overlap_top);
                        // get the currently displayed TextView text as array. This way you can update the dual counter correctly
                        String[] dual_counter = hold_text.getText().toString().split(" ");
                        // get the last part of the array, because that is the counter of previous problem
                        String previous_counter = dual_counter[dual_counter.length-1];
                        hold_text.setText(getString(R.string.two_selected_problems_counter, previous_counter, String.valueOf(i +1)));
                    }
                    // check if the current hold is already being shared and just change the counter text
                    else if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(),R.drawable.hold_background_overlap_top, null).getConstantState())){
                        hold_text.setText(String.valueOf(i +1));
                    }
                    // if the hold has not been displayed yet, set the color and counter text
                    else{
                        hold_image.setBackgroundResource(R.drawable.hold_background_red);
                        hold_text.setText(String.valueOf(i +1));
                    }

                    break;
            }
        }
    }
    /*
    Function to display primary sequence on the climbing wall
    */
    public void showSecondDatabaseProblem(String sequence, String sequence_counters, String sequence_tags){

        String[] holds = sequence.split(",");
        String[] hold_counters = sequence_counters.split(",");
        String[] hold_tags = sequence_tags.split(",");

        for (int i = 0; i < holds.length; i ++){
            ImageView hold_image = (ImageView) mainWall.findViewById(Integer.parseInt(holds[i]));
            TextView hold_text = (TextView) mainWall.findViewById(Integer.parseInt(hold_counters[i]));
            // select the correct color for the hold background
            switch (hold_tags[i]){
                case "green":
                    // check if current hold is shared by the already displayed sequence
                    if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_green, null).getConstantState())){
                        hold_image.setBackgroundResource(R.drawable.hold_background_overlap_start);
                        // get the currently displayed TextView text as array. This way you can update the dual counter correctly
                        String[] dual_counter = hold_text.getText().toString().split(" ");
                        // get the last part of the array, because that is the counter of previous problem
                        String previous_counter = dual_counter[dual_counter.length-1];
                        hold_text.setText(getString(R.string.two_selected_problems_counter, previous_counter, String.valueOf(i +1)));
                    }
                    // check if the current hold is already being shared and just change the counter text
                    else if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_overlap_start, null).getConstantState())){
                        hold_text.setText(String.valueOf(i +1));
                    }
                    // if the hold has not been displayed yet, set the color and counter text
                    else{
                        hold_image.setBackgroundResource(R.drawable.hold_background_yellow);
                        hold_text.setText(String.valueOf(i +1));
                    }

                    break;
                case "blue":
                    // check if current hold is shared by the already displayed sequence
                    if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_blue, null).getConstantState())){
                        hold_image.setBackgroundResource(R.drawable.hold_background_overlap_intermediate);
                        // get the currently displayed TextView text as array. This way you can update the dual counter correctly
                        String[] dual_counter = hold_text.getText().toString().split(" ");
                        // get the last part of the array, because that is the counter of previous problem
                        String previous_counter = dual_counter[dual_counter.length-1];
                        hold_text.setText(getString(R.string.two_selected_problems_counter, previous_counter, String.valueOf(i +1)));
                    }
                    // check if the current hold is already being shared and just change the counter text
                    else if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(),R.drawable.hold_background_overlap_intermediate, null).getConstantState())){
                        hold_text.setText(String.valueOf(i +1));
                    }
                    // if the hold has not been displayed yet, set the color and counter text
                    else{
                        hold_image.setBackgroundResource(R.drawable.hold_background_cyan);
                        hold_text.setText(String.valueOf(i +1));
                    }

                    break;
                case "red":
                    // check if current hold is shared by the already displayed sequence
                    if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_red, null).getConstantState())){
                        hold_image.setBackgroundResource(R.drawable.hold_background_overlap_top);
                        // get the currently displayed TextView text as array. This way you can update the dual counter correctly
                        String[] dual_counter = hold_text.getText().toString().split(" ");
                        // get the last part of the array, because that is the counter of previous problem
                        String previous_counter = dual_counter[dual_counter.length-1];
                        hold_text.setText(getString(R.string.two_selected_problems_counter, previous_counter, String.valueOf(i +1)));

                    }
                    // check if the current hold is already being shared and just change the counter text
                    else if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_overlap_top, null).getConstantState())){
                        hold_text.setText(String.valueOf(i +1));
                    }
                    // if the hold has not been displayed yet, set the color and counter text
                    else{
                        hold_image.setBackgroundResource(R.drawable.hold_background_magenta);
                        hold_text.setText(String.valueOf(i +1));
                    }

                    break;
            }
        }
    }

    public void clearSpecificProblem(String sequence, String sequence_counters, String sequence_tags){
        String[] holds = sequence.split(",");
        String[] hold_counters = sequence_counters.split(",");
        String[] hold_tags = sequence_tags.split(","); // currently not used, but might be useful in the future

        for (int i = 0; i < holds.length; i ++){
            ImageView hold_image = (ImageView) mainWall.findViewById(Integer.parseInt(holds[i]));
            TextView hold_text = (TextView) mainWall.findViewById(Integer.parseInt(hold_counters[i]));

            // check if the hold that you're trying to reset overlaps with another hold - START
            if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_overlap_start, null).getConstantState())){
                if(!primary_problem_color){
                    // erase the first part of the counter as it represents the problem we are trying to delete
                    String[] dual_counter = hold_text.getText().toString().split(" ");
                    // get the last part of the array, because that is the counter of the problem we are trying to delete
                    String current_counter = dual_counter[dual_counter.length-1];
                    hold_text.setText(current_counter);
                    hold_image.setTag("green"); // reset the hold tag
                    hold_image.setBackgroundResource(R.drawable.hold_background_yellow); // reset the hold background
                }
                else{
                    // erase the first part of the counter as it represents the problem we are trying to delete
                    String[] dual_counter = hold_text.getText().toString().split(" ");
                    // get the last part of the array, because that is the counter of the problem we are trying to delete
                    String current_counter = dual_counter[dual_counter.length-1];
                    hold_text.setText(current_counter);
                    hold_image.setTag("green"); // reset the hold tag
                    hold_image.setBackgroundResource(R.drawable.hold_background_green); // reset the hold background
                }
            }

            // check if the hold that you're trying to reset overlaps with another hold - INTERMEDIATE
            else if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_overlap_intermediate, null).getConstantState())){
                if(!primary_problem_color){
                    // erase the first part of the counter as it represents the problem we are trying to delete
                    String[] dual_counter = hold_text.getText().toString().split(" ");
                    // get the last part of the array, because that is the counter of the problem we are trying to delete
                    String current_counter = dual_counter[dual_counter.length-1];
                    hold_text.setText(current_counter);
                    hold_image.setTag("blue"); // reset the hold tag
                    hold_image.setBackgroundResource(R.drawable.hold_background_cyan); // reset the hold background
                }
                else{
                    // erase the first part of the counter as it represents the problem we are trying to delete
                    String[] dual_counter = hold_text.getText().toString().split(" ");
                    // get the last part of the array, because that is the counter of the problem we are trying to delete
                    String current_counter = dual_counter[dual_counter.length-1];
                    hold_text.setText(current_counter);
                    hold_image.setTag("green"); // reset the hold tag
                    hold_image.setBackgroundResource(R.drawable.hold_background_blue); // reset the hold background
                }
            }

            // check if the hold that you're trying to reset overlaps with another hold - TOP
            else if(Objects.equals(hold_image.getBackground().getConstantState(), ResourcesCompat.getDrawable(getResources(), R.drawable.hold_background_overlap_top, null).getConstantState())){
                if(!primary_problem_color){
                    // erase the first part of the counter as it represents the problem we are trying to delete
                    String[] dual_counter = hold_text.getText().toString().split(" ");
                    // get the last part of the array, because that is the counter of the problem we are trying to delete
                    String current_counter = dual_counter[dual_counter.length-1];
                    hold_text.setText(current_counter);
                    hold_image.setTag("red"); // reset the hold tag
                    hold_image.setBackgroundResource(R.drawable.hold_background_magenta); // reset the hold background
                }
                else{
                    // erase the first part of the counter as it represents the problem we are trying to delete
                    String[] dual_counter = hold_text.getText().toString().split(" ");
                    // get the last part of the array, because that is the counter of the problem we are trying to delete
                    String current_counter = dual_counter[dual_counter.length-1];
                    hold_text.setText(current_counter);
                    hold_image.setTag("red"); // reset the hold tag
                    hold_image.setBackgroundResource(R.drawable.hold_background_red); // reset the hold background
                }
            }
            // completely reset the hold
            else{
                hold_text.setText(""); // reset the hold counter
                hold_image.setTag("empty"); // reset the hold tag
                hold_image.setBackgroundResource(R.drawable.hold_background_empty); // reset the hold background
            }

        }
    }

    /*
    Function that resets the values of ImageView and TextViews that are part of the climbing wall
     */
    public void clearWall(){

        for(String width: width){
            for(String height: height){
                // get ID of the ImageView
                int hold_id = getResources().getIdentifier(width+height, "id", getPackageName());
                ImageView selected_hold = (ImageView) mainWall.findViewById(hold_id);

                selected_hold.setBackgroundResource(R.drawable.hold_background_empty);

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
                firstSequenceSender = new FirstSequenceSender(getApplicationContext());
                secondSequenceSender = new SecondSequenceSender(getApplicationContext());

                //check if application is about to shutdown and switch of all LEDs. This is useful when the user quits the App
                if (!opened){
                    firstSequenceSender.execute("hide_all");
                }
                // check if HashMap current_sequence_data is empty - there is no selected problem
                if(current_sequence_data.isEmpty()){
                    Toast.makeText(getApplicationContext(),"ERROR: You need to choose the problem first!" , Toast.LENGTH_LONG).show();
                    firstSequenceSender.execute("There is no problem selected");
                }
                // calculate the correct values for LEDs and send them to Arduino UNO
                else{
                    // if there are two problems displayed on the wall
                    if(!second_sequence_data.isEmpty()){
                        // send data from the FIRST SEQUENCE
                        String [] current_sequence = current_sequence_data.get("Sequence").split(",");
                        StringBuilder current_sequence_id = new StringBuilder();
                        String prefix = "";
                        for (String id : current_sequence){
                            current_sequence_id.append(prefix);
                            String led_number = translate_coordinate(getResources().getResourceEntryName(Integer.parseInt(id)));
                            current_sequence_id.append(led_number);
                            prefix = ",";
                        }

                        String current_sequence_info = current_sequence_id.toString() + ";" + current_sequence_data.get("Sequence_tags") + ";" + "first";
                        firstSequenceSender.execute(current_sequence_info);


                        // send data from the SECOND SEQUENCE
                        String [] second_sequence = second_sequence_data.get("Sequence").split(",");
                        StringBuilder second_sequence_id = new StringBuilder();
                        prefix = "";
                        for (String id : second_sequence){
                            second_sequence_id.append(prefix);
                            String led_number = translate_coordinate(getResources().getResourceEntryName(Integer.parseInt(id)));
                            second_sequence_id.append(led_number);
                            prefix = ",";
                        }
                        String second_sequence_info = second_sequence_id.toString() + ";" + second_sequence_data.get("Sequence_tags") + ";" + "second";
                        secondSequenceSender.execute(second_sequence_info);
                    }
                    // if there is one problem displayed on the wall
                    else{
                        String [] sequence = current_sequence_data.get("Sequence").split(",");
                        StringBuilder sequence_id = new StringBuilder();
                        String prefix = "";
                        for (String id : sequence){
                            sequence_id.append(prefix);
                            String led_number = translate_coordinate(getResources().getResourceEntryName(Integer.parseInt(id)));
                            sequence_id.append(led_number);
                            prefix = ",";
                        }
                        String sequence_info = sequence_id.toString() + ";" + current_sequence_data.get("Sequence_tags") + ";" + "first";
                        firstSequenceSender.execute(sequence_info);
                    }

                }

    }

    /*
    Function that maps sequence holds to corresponding LED lights
     */

    public String translate_coordinate(String hold_id){
        String hold_letter = hold_id.substring(0,1); // get the letter part of the ID
        int hold_number = Integer.parseInt(hold_id.substring(1)); // get the number part of the ID as integer
        /*
        calculate the corresponding led number
        (you need to subtract 1, because LED assignments start with 0
                */
        int led_number = hold_led_numbers.get(hold_letter) + hold_number - 1;

        return String.valueOf(led_number); // convert the led number to string
    }

}

