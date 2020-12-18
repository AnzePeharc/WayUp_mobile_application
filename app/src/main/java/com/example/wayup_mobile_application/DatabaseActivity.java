package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
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
    MainActivity main = new MainActivity();
    // variable for interacting with DatabaseHelper
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    // variables for filling the ListView
    ArrayList<Problem> arrayOfProblems = new ArrayList<Problem>();
    ProblemAdapter problemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_activity);

        // set variable for drawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // fill the table with database content
        problemAdapter =  new ProblemAdapter(this, arrayOfProblems);
        problemTable = (ListView) findViewById(R.id.problem_table);
        problemTable.setAdapter(problemAdapter); // bind the adapter to the arrayList
        // Add OnItemClickListener for ListView
        problemTable.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                Problem selected_problem = (Problem) adapter.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), selected_problem.getSequence(), Toast.LENGTH_LONG).show();
                sendProblemSequence(DatabaseActivity.this, MainActivity.class, selected_problem.getSequence(), selected_problem.getSequence_tags(), selected_problem.getSequence_counters());

                // TODO show the clicked problem on the mainScreen climbing wall
            }
        });

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
                Problem new_problem = new Problem(cursor.getInt(0),cursor.getString(1) ,cursor.getString(2),cursor.getString(3),
                        cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7));
                arrayOfProblems.add(new_problem);
            }
        }

    }

    public static void sendProblemSequence(Activity activity, Class aClass, String sequence, String sequence_tags, String sequence_counters) {
        // Initialize intent
        Intent intent = new Intent(activity, aClass);
        // Set Flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mainScreen_sequence", sequence);
        intent.putExtra("mainScreen_sequence_tags", sequence_tags);
        intent.putExtra("mainScreen_sequence_counters", sequence_counters);
        // Start the activity
        activity.startActivity(intent);
    }
}
