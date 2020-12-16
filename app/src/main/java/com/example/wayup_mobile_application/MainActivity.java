package com.example.wayup_mobile_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    // variables for loading problem from Database
    GridLayout mainWall;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ArrayList<Problem> allProblems = new ArrayList<Problem>();
    int current_problem_index;

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
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set default state
        bottomNavigationView.setSelectedItemId(R.id.show_problem);
        // set ActionListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.load_problem:
                        // TODO implement correct graphics for loading problem from Database
                        Problem current_problem = allProblems.get(current_problem_index);
                        String sequence = current_problem.getSequence(); // get holds
                        String sequence_counters = current_problem.getSequence_counters(); //get hold_counters
                        String[] holds = sequence.split(",");
                        String[] hold_counters = sequence_counters.split(",");
                        System.out.println(sequence_counters.length());
                        for (int i = 0; i < holds.length; i ++){
                            System.out.println(hold_counters[i]);

                            ImageView hold_image = (ImageView) mainWall.findViewById(Integer.parseInt(holds[i]));
                            TextView hold_text = (TextView) mainWall.findViewById(Integer.parseInt(hold_counters[i]));
                            hold_text.setText(String.valueOf(i +1));
                            ShapeDrawable first = new ShapeDrawable(new OvalShape());
                            first.setIntrinsicHeight(hold_image.getHeight() / 2);
                            first.setIntrinsicWidth(hold_image.getHeight() / 2);
                            first.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                                    R.color.hold_green_background, null));;
                            hold_image.setBackground(first);

                        }

                        // check if the current_problem_index is still in range of array size - Avoid indexOutofBounds
                        if(current_problem_index < allProblems.size()-1){
                            current_problem_index ++; // increment the index to show new problem when load_problem is pressed again
                        }
                        // if the problem_index is equal to array size, reset it to zero - that way you start showing problems from the beginning again
                        else{
                            current_problem_index = 0;
                        }
                        return true;
                    case R.id.show_problem:
                        return true;

                    case R.id.add_problem:

                        startActivity(new Intent(getApplicationContext(), SetProblemActivity.class));
                        return true;
                }
                return false;
            }
        });


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
                Problem new_problem = new Problem(cursor.getInt(0),cursor.getString(1),cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6));
                allProblems.add(new_problem);
            }
        }

    }

}
