package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

// Google Firebase imports
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddProblemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    // Array with Dropdown values. Currently not used, because the values are loaded from string resource file
    String[] grades = {"5a", "5a+", "5b", "5b+", "5c", "5c+", "6a", "6a+", "6b", "6b+", "6c", "6c+",
            "7a", "7a+", "7b", "7b+", "7c", "7c+", "8a", "8a+", "8b", "8b+", "8c", "8c+"};

    // Button variables
    Button back;
    Button discard;
    Button save;

    // Dropdown variable
    Spinner spinner;

    // variable for alert PopUp
    AlertDialog dialog;

    // TextEdit layout variables
    TextInputLayout name;
    TextInputLayout setter;
    TextInputLayout comment;
    TextInputEditText editText_name, editText_setter, editText_comment;

    // variable for interacting with DatabaseHelper
    DatabaseHelper databaseHelper = new DatabaseHelper(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_problem_activity);
        // FIREBASE START
        FirebaseHelper fbhelper = new FirebaseHelper();
        // FIREBASE END

        editText_name = findViewById(R.id.name_input_edittext);
        // Soft hide keyboard if user clicks outside EditText - NAME text_field
        editText_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        // Soft hide keyboard if user clicks outside EditText - SETTER text_field
        editText_setter = findViewById(R.id.setter_input_edittext);
        editText_setter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        // Soft hide keyboard if user clicks outside EditText - COMMENT text_field
        editText_comment = findViewById(R.id.comment_input_edittext);
        editText_comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        // assign views to the variables
        back = (Button) findViewById(R.id.back_button);
        discard = (Button) findViewById(R.id.discard_button);
        save = (Button) findViewById(R.id.save_button);
        name = (TextInputLayout) findViewById(R.id.name_input);
        setter = (TextInputLayout) findViewById(R.id.setter_input);
        comment = (TextInputLayout) findViewById(R.id.comment_input);
        spinner = (Spinner) findViewById(R.id.grade_spinner);
        // set the default value of the spinner to the first element from array - grades
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Popup window for discarding the problem and redirecting user back to MainScreen
        discard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddProblemActivity.this);

                builder.setTitle("Discarding the problem!");
                builder.setMessage("Are you sure you want to discard it?");
                builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddProblemActivity.this, MainActivity.class);
                        // Set Flag
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // send current_problem_data to MainActivity to update the UI
                        intent.putExtra("mainScreen_sequence", getIntent().getStringExtra("addScreen_sequence"));
                        intent.putExtra("mainScreen_sequence_tags", getIntent().getStringExtra("addScreen_sequence_tags"));
                        intent.putExtra("mainScreen_sequence_counters", getIntent().getStringExtra("addScreen_sequence_counters"));
                        intent.putExtra("mainScreen_sequence_name", getIntent().getStringExtra("addScreen_sequence_name"));
                        intent.putExtra("mainScreen_sequence_grade", getIntent().getStringExtra("addScreen_sequence_grade"));
                        intent.putExtra("mainScreen_sequence_setter", getIntent().getStringExtra("addScreen_sequence_setter"));
                        intent.putExtra("mainScreen_sequence_comment", getIntent().getStringExtra("addScreen_sequence_comment"));
                        // Start the activity
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.show();

            }
        });

        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(name.getEditText().getText().equals("")){
                    Toast.makeText(getApplicationContext(),"You need to add the name of the problem!" , Toast.LENGTH_LONG).show();
                }
                if(setter.getEditText().getText().equals("")){
                    Toast.makeText(getApplicationContext(),"You need to add the name of the problem!" , Toast.LENGTH_LONG).show();
                }
                if(spinner.getSelectedItem().toString().equals("Select grade of the problem")){
                    Toast.makeText(getApplicationContext(),"You must choose the grade of the problem from dropdown!" , Toast.LENGTH_LONG).show();
                }
                else{

                    String problem_sequence = getIntent().getStringExtra("Problem_sequence"); // get the sequence from SetProblemActivity
                    String problem_sequence_counters = getIntent().getStringExtra("Problem_sequence_counters"); // get the sequence_counters from SetProblemActivity
                    String problem_sequence_tags = getIntent().getStringExtra("Problem_sequence_tags"); // get the sequence_tags from SetProblemActivity

                    // insert data into Firebase
                    Problem problem = new Problem(1, problem_sequence, problem_sequence_tags,problem_sequence_counters, name.getEditText().getText().toString().trim(),
                            spinner.getSelectedItem().toString(), setter.getEditText().getText().toString().trim(), comment.getEditText().getText().toString().trim());

                    fbhelper.add(problem).addOnSuccessListener(suc ->
                        {
                            Toast.makeText(getApplicationContext(), "Record is inserted", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(er ->
                        {
                            Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    /*
                    // insert data into Local Database
                    boolean insert_status = databaseHelper.insertData(problem_sequence, problem_sequence_tags,problem_sequence_counters, name.getEditText().getText().toString().trim(),
                            spinner.getSelectedItem().toString(), setter.getEditText().getText().toString().trim(), comment.getEditText().getText().toString().trim());


                    // Alert user about the status of the added problem
                    if(insert_status){
                        Toast.makeText(getApplicationContext(),"Problem successfully added.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Failed to add the problem.", Toast.LENGTH_LONG).show();
                    }

                     */
                    // Redirect the user back to the mainActivity
                    MainActivity.redirectActivity(AddProblemActivity.this, MainActivity.class);
                }

            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getApplicationContext(),grades[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Method that hides the keyboard when user presses outside the EditText
    private void hideKeyboard(View view) {

        //Find the currently focused view, so we can grab the correct window token from it.
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}
