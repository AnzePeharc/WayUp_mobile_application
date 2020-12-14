package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;

public class DatabaseActivity extends Activity {

    DrawerLayout drawerLayout;
    ListView problemTable;

    // variable for interacting with DatabaseHelper
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    // variables for filling the ListView
    ArrayList<Problem> arrayOfProblems = new ArrayList<Problem>();
    ProblemAdapter problemAdapter = new ProblemAdapter(this, arrayOfProblems);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_activity);

        // set variable for drawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // fill the table with database content
        problemTable = (ListView) findViewById(R.id.problem_table);
        problemTable.setAdapter(problemAdapter); // bind the adapter to the arrayList

        // call function for filling the table with elements
        fillTable(this);

    }

    public void ClickMenu(View view) {
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        // close Drawer
        MainActivity.closeDrawer(drawerLayout);

    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        // close Drawer layout
        //check current state of the Drawer
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // close the Drawer
            drawerLayout.closeDrawer((GravityCompat.START));
        }
    }

    public void ClickHome(View view) {
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickDatabase(View view) {
        recreate();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // close Drawer
        MainActivity.closeDrawer(drawerLayout);

    }

    // function for filling the table with problem entries
    public void fillTable(Context context){

        // get the cursor from Database
        Cursor cursor = databaseHelper.getAllData();

        // check if database is empty
        if(cursor.getCount() == 0){
            Toast.makeText(this,"ERROR: Empty database." , Toast.LENGTH_LONG).show();
        }
        else{
            while(cursor.moveToNext()){
                Problem new_problem = new Problem(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5));
                arrayOfProblems.add(new_problem);
            }
            Toast.makeText(this,"Database was successfully loaded!" , Toast.LENGTH_LONG).show();
        }

    }
}
