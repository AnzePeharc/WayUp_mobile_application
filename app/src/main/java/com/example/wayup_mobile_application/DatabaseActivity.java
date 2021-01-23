package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;

public class DatabaseActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    ListView problemTable;
    // variable for interacting with DatabaseHelper
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    // variables for filling the ListView
    ArrayList<Problem> arrayOfProblems = new ArrayList<Problem>(); // ArrayList containing all the problems
    ArrayList<Problem> arrayOfProblemsAsc = new ArrayList<Problem>(); // ArrayList containing all the problem sorted ascending
    ArrayList<Problem> arrayOfProblemsDesc = new ArrayList<Problem>();// ArrayList containing all the problem sorted descending
    ProblemAdapter problemAdapter; // adapter that displays the problems from database in ListView
    SearchView search_problems; // SearchView that allows filtering of the problems by name
    TextView problem_count; // TextView to display number of problems in database
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_activity);

        // set variable for problem_count
        problem_count = (TextView) findViewById(R.id.problem_count);

        // set variable for drawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // call function for filling the table with elements
        fillTable(this);
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
                sendProblemSequence(DatabaseActivity.this, MainActivity.class,
                        selected_problem.getSequence(), selected_problem.getSequence_tags(),
                        selected_problem.getSequence_counters(), selected_problem.getName(), selected_problem.getGrade(), selected_problem.getSetter(), selected_problem.getComment());

            }
        });

        // search menu variable for filtering problems by name
        search_problems = (SearchView) findViewById(R.id.search_menu);

        // Added onTextChange listener to listen on user input and filter the results
        search_problems.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                problemAdapter.getFilter().filter(newText);
                return false;
            }
        });


        //TODO: implement system for filtering the DataBase entries
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().getStringExtra("sortType") != null){
            sortByGrade(getIntent().getStringExtra("sortType"), getIntent().getStringExtra("minGrade"), getIntent().getStringExtra("maxGrade"));

        }
        else{
            System.out.println("No data has been sent!");
        }
    }


    public void ClickMenu(View view) {
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {

        MainActivity.redirectActivity(this, MainActivity.class);

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

        // get the cursor from Database for AllData
        Cursor cursor = databaseHelper.getAllData();
        // update the problem_count TextView with the number of all problems
        problem_count.setText(getString(R.string.problem_count, cursor.getCount()));

        // get the cursor from Database for AllData sorted ascending
        Cursor cursorAsc = databaseHelper.getAllDataAsc();
        // get the cursor from Database for AllData sorted descending
        Cursor cursorDesc = databaseHelper.getAllDataDesc();

        // check if Cursor contains data
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
        // check if CursorAsc contains data
        if(cursorAsc.getCount() == 0){
            Toast.makeText(this,"ERROR: Empty database." , Toast.LENGTH_LONG).show();
        }
        else{
            while(cursorAsc.moveToNext()){
                Problem new_problem = new Problem(cursorAsc.getInt(0),cursorAsc.getString(1) ,cursorAsc.getString(2),cursorAsc.getString(3),
                        cursorAsc.getString(4), cursorAsc.getString(5),
                        cursorAsc.getString(6), cursorAsc.getString(7));
                arrayOfProblemsAsc.add(new_problem);
            }
        }

        // check if CursorDesc contains data
        if(cursorDesc.getCount() == 0){
            Toast.makeText(this,"ERROR: Empty database." , Toast.LENGTH_LONG).show();
        }
        else{
            while(cursorDesc.moveToNext()){
                Problem new_problem = new Problem(cursorDesc.getInt(0),cursorDesc.getString(1) ,cursorDesc.getString(2),cursorDesc.getString(3),
                        cursorDesc.getString(4), cursorDesc.getString(5),
                        cursorDesc.getString(6), cursorDesc.getString(7));
                arrayOfProblemsDesc.add(new_problem);
            }
        }

    }
    public void updateTable(Context context){

    }

    public static void sendProblemSequence(Activity activity, Class aClass, String sequence, String sequence_tags, String sequence_counters, String sequence_name, String sequence_grade, String sequence_setter, String sequence_comment) {
        // Initialize intent
        Intent intent = new Intent(activity, aClass);
        // Set Flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mainScreen_sequence", sequence);
        intent.putExtra("mainScreen_sequence_tags", sequence_tags);
        intent.putExtra("mainScreen_sequence_counters", sequence_counters);
        intent.putExtra("mainScreen_sequence_name", sequence_name);
        intent.putExtra("mainScreen_sequence_grade", sequence_grade);
        intent.putExtra("mainScreen_sequence_setter", sequence_setter);
        intent.putExtra("mainScreen_sequence_comment", sequence_comment);
        // Start the activity
        activity.startActivity(intent);
    }

    public void sortByGrade(String sorted, String minGrade, String maxGrade){

        ArrayList<Problem> temp = new ArrayList<Problem>();
        if(sorted.equals("descending")){
            for(Problem problem : arrayOfProblemsDesc){
                if(problem.getGrade().compareTo(minGrade) >= 0 & problem.getGrade().compareTo(maxGrade) <= 0){
                    temp.add(problem);
                }
            }
        }
        else{
            for(Problem problem : arrayOfProblemsAsc) {
                if (problem.getGrade().compareTo(minGrade) >= 0 & problem.getGrade().compareTo(maxGrade) <= 0) {
                    temp.add(problem);
                }
            }
        }
        problemAdapter =  new ProblemAdapter(this, temp);
        problemTable.setAdapter(problemAdapter);

    }

    public void FilterOptions(View view){
        MainActivity.redirectActivity(DatabaseActivity.this, FilterActivity.class);
    }
}
