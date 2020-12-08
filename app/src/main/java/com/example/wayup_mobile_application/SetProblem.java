package com.example.wayup_mobile_application;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class SetProblem extends Activity{

    Button cancel;
    Button next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_problem_activity);

        cancel = findViewById(R.id.cancel_button);
        next = findViewById(R.id.next_button);

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(SetProblem.this, MainActivity.class);
            }
        });

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(SetProblem.this, AddProblem.class);
            }
        });
    }


}
