package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class DatabaseActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    ListView problemTable;
    SwitchMaterial two_problem_option;
    // variable for alert PopUp
    AlertDialog dialog;
    // variable for interacting with DatabaseHelper
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    // variable for interacting with FirebaseHelper
    FirebaseHelper fbhelper = new FirebaseHelper();

    // variables for filling the ListView
    ArrayList<Problem> arrayOfProblems = new ArrayList<Problem>(); // ArrayList containing all the problems
    ArrayList<Problem> arrayOfProblemsAsc = new ArrayList<Problem>(); // ArrayList containing all the problem sorted ascending
    ArrayList<Problem> arrayOfProblemsDesc = new ArrayList<Problem>();// ArrayList containing all the problem sorted descending
    ArrayList<Problem> sortedArray = new ArrayList<Problem>();// ArrayList containing all the sorted problems (desc or asc)
    ArrayList<Problem> arrayOfProblemsFirebase = new ArrayList<Problem>();// ArrayList containing all the problems in Firebase
    ProblemAdapter problemAdapter; // adapter that displays the problems from database in ListView
    SearchView search_problems; // SearchView that allows filtering of the problems by name
    TextView problem_count; // TextView to display number of problems in database
    ArrayList<Integer> selected_problem_positions = new ArrayList<Integer>(); // arrayList for holding the two selected problems positions (int)
    ArrayList<Problem> selected_problems = new ArrayList<Problem>(); // arrayList for holding the two selected problems
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_activity);

        // set variable for problem_count
        problem_count = (TextView) findViewById(R.id.problem_count);

        // set variable for drawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set variable for the ListView, which contains the problems
        problemTable = (ListView) findViewById(R.id.problem_table);

        problemAdapter =  new ProblemAdapter(this, arrayOfProblemsFirebase);
        problemTable.setAdapter(problemAdapter); // bind the adapter to the arrayList
        // START loading all the data from the Firebase
        fbhelper.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Problem fb_problem = data.getValue(Problem.class);

                        arrayOfProblemsFirebase.add(fb_problem);
                    }
                    // update the problem_count TextView with the number of all problems
                    problem_count.setText(getString(R.string.problem_count, arrayOfProblemsFirebase.size()));
                    problemAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getApplicationContext(),"ERROR: Empty Database." , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // END Firebase loading
        // call function for filling the table with elements
        // fillTableFirebaseDesc(this);
        //fillTable(this);
        // fill the table with database content

        // Initialize problem Switch
        two_problem_option = findViewById(R.id.two_problems_switch);
        two_problem_option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // check if the switch has been turned of and reset the adapter of the problems
                if(!isChecked){
                    problemAdapter =  new ProblemAdapter(getApplicationContext(), arrayOfProblemsFirebase);
                    problemTable.setAdapter(problemAdapter); // reset the problemAdapter in order to restore the basic colors of background
                    selected_problem_positions = new ArrayList<Integer>(); // reset arrayList for holding currently selected two problems
                    selected_problems = new ArrayList<Problem>(); // reset arrayList for holding instances of selected problems
                }
                closeDrawer(drawerLayout); // close the drawer
            }
        });

        // Add OnItemClickListener for ListView
        problemTable.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                // if the switch is checked let the user pick two problems and then showing them on the wall at the same time
                if(two_problem_option.isChecked()){
                    // change the background of selected table_item back to normal  to indicate de-selection
                    if(Objects.equals(problemTable.getChildAt(position).getBackground().getConstantState(), getResources().getDrawable(R.drawable.customborder_blue_full).getConstantState())){
                        problemTable.getChildAt(position).setBackgroundResource(R.drawable.customborder_blue); // set the background color to blue border
                        for (int i = 0; i < selected_problem_positions.size(); i++) {
                            if(selected_problem_positions.get(i) == position){
                                selected_problem_positions.remove(i);
                                selected_problems.remove(i);

                            }
                        }
                    }
                    // change the background of selected table_item to blue to indicate selection
                    else{
                        if(selected_problem_positions.size() < 2){
                            problemTable.getChildAt(position).setBackgroundResource(R.drawable.customborder_blue_full); // set the background color to blue
                            selected_problem_positions.add(position);
                            selected_problems.add((Problem) adapter.getItemAtPosition(position));
                        }
                        else{
                            problemTable.getChildAt(selected_problem_positions.get(0)).setBackgroundResource(R.drawable.customborder_blue); // reset the background color of the first added problem
                            selected_problem_positions.remove(0); // remove the first problem from arrayList, because there can only be two problems selected at once
                            selected_problems.remove(0);
                            selected_problem_positions.add(position); // add the next problem to the arrayList
                            selected_problems.add((Problem) adapter.getItemAtPosition(position));
                            problemTable.getChildAt(selected_problem_positions.get(1)).setBackgroundResource(R.drawable.customborder_blue_full); // set the background color to blue of the last selected problem
                        }

                    }
                }
                else{
                    Problem selected_problem = (Problem) adapter.getItemAtPosition(position);
                    //Toast.makeText(getApplicationContext(), selected_problem.getSequence(), Toast.LENGTH_LONG).show();
                    sendProblemSequence(DatabaseActivity.this, MainActivity.class,
                            selected_problem.getSequence(), selected_problem.getSequence_tags(),
                            selected_problem.getSequence_counters(), selected_problem.getName(), selected_problem.getGrade(), selected_problem.getSetter(), selected_problem.getComment(), String.valueOf(position));
                }

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

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().getStringExtra("sortType") != null){
            System.out.println("Pride noter!");
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
        finish();
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
        finish();
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
    public void fillTableFirebaseDesc(Context context){

        Query query = fbhelper.get().orderByChild("grade");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        Problem fb_problem = data.getValue(Problem.class);
                        System.out.println("Problem " + fb_problem.getName());
                        arrayOfProblemsDesc.add(fb_problem);
                    }
                    System.out.println("Length: "+ arrayOfProblemsDesc.size());
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR: Empty Database.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    //   function for updating the table of content
    public void updateTable(Context context){

    }

    public static void sendProblemSequence(Activity activity, Class aClass, String sequence, String sequence_tags, String sequence_counters, String sequence_name, String sequence_grade, String sequence_setter, String sequence_comment, String position) {
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
        intent.putExtra("mainScreen_sequence_position", position);
        // Start the activity
        activity.startActivity(intent);
    }

    public void sortByGrade(String sorted, String minGrade, String maxGrade){

        problemAdapter =  new ProblemAdapter(this, sortedArray);
        problemTable.setAdapter(problemAdapter);

        Query query = fbhelper.get().orderByChild("grade");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        Problem fb_problem = data.getValue(Problem.class);
                        System.out.println("Problem " + fb_problem.getName());
                        sortedArray.add(fb_problem);
                    }
                    // sort problems descending
                    if(sorted.equals("ascending")){
                        Collections.reverse(sortedArray);
                        Problem test = sortedArray.get(sortedArray.size() - 1);
                        System.out.println("LastProblem: " + test.getGrade());
                    }
                    problem_count.setText(getString(R.string.problem_count, sortedArray.size()));
                    problemAdapter.notifyDataSetChanged();
                    System.out.println("Length: "+ sortedArray.size());
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR: Empty Database.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void FilterOptions(View view){
        MainActivity.redirectActivity(DatabaseActivity.this, FilterActivity.class);
    }
    public void ShowTwoProblems(View view){
        if(selected_problems.size() == 2){
            Problem first_problem = (Problem) selected_problems.get(0);
            Problem second_problem = (Problem) selected_problems.get(1);

            Intent intent = new Intent(DatabaseActivity.this, MainActivity.class);
            // Set Flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // send data for the first problem
            intent.putExtra("mainScreen_sequence", first_problem.getSequence());
            intent.putExtra("mainScreen_sequence_tags", first_problem.getSequence_tags());
            intent.putExtra("mainScreen_sequence_counters", first_problem.getSequence_counters());
            intent.putExtra("mainScreen_sequence_name", first_problem.getName());
            intent.putExtra("mainScreen_sequence_grade", first_problem.getGrade());
            intent.putExtra("mainScreen_sequence_setter", first_problem.getSetter());
            intent.putExtra("mainScreen_sequence_comment", first_problem.getComment());

            // send data for the second problem
            intent.putExtra("mainScreen_second_sequence", second_problem.getSequence());
            intent.putExtra("mainScreen_second_sequence_tags", second_problem.getSequence_tags());
            intent.putExtra("mainScreen_second_sequence_counters", second_problem.getSequence_counters());
            intent.putExtra("mainScreen_second_sequence_name", second_problem.getName());
            intent.putExtra("mainScreen_second_sequence_grade", second_problem.getGrade());
            intent.putExtra("mainScreen_second_sequence_setter", second_problem.getSetter());
            intent.putExtra("mainScreen_second_sequence_comment", second_problem.getComment());
            // Start the activity
            DatabaseActivity.this.startActivity(intent);
        }
        else{
            final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DatabaseActivity.this);
            builder.setTitle("Try Again!");
            builder.setMessage("You need to select two problems.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog = builder.show();
        }
    }
}
