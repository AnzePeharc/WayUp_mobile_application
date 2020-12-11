package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.ToDoubleBiFunction;

public class AddProblem extends Activity implements AdapterView.OnItemSelectedListener{

    String[] grades = { "Select grade of the problem","4a", "4a+", "4b", "4b+", "4c", "4c+", "5a", "5a+", "5b", "5b+", "5c", "5c+", "6a", "6a+", "6b", "6b+", "6c", "6c+",
            "7a", "7a+", "7b", "7b+", "7c", "7c+", "8a", "8a+", "8b", "8b+", "8c", "8c+"};
    Button back;
    Button discard;
    Button save;
    Spinner spinner;
    AlertDialog dialog;
    TextInputLayout name;
    TextInputLayout setter;
    TextInputLayout comment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_problem_activity);
        // assign views to the variables
        back = (Button) findViewById(R.id.back_button);
        discard = (Button) findViewById(R.id.discard_button);
        save = (Button) findViewById(R.id.save_button); //TODO implement onclicklistener for adding problems to the database
        name = (TextInputLayout) findViewById(R.id.name_input);
        setter = (TextInputLayout) findViewById(R.id.setter_input);
        comment = (TextInputLayout) findViewById(R.id.comment_input);
        spinner = (Spinner) findViewById(R.id.grade_spinner);
        // set the default value of the spinner to the first element from array - grades
        spinner.setSelection(0);
        // set onclicklistener for spinner
        spinner.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter spinner_adapter = new ArrayAdapter(this,R.layout.spinner_item,grades);
        // spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(spinner_adapter);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(AddProblem.this, SetProblem.class);
            }
        });
        discard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddProblem.this);

                builder.setTitle("Discarding the problem!");
                builder.setMessage("Are you sure you want to discard it?");
                builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.redirectActivity(AddProblem.this, MainActivity.class);
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

            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),grades[position] , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
