package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class SetProblemActivity extends Activity{

    Button cancel;
    Button next;

    // variables for interactive climbing wall
    ArrayList<ImageView> selected_holds = new ArrayList<>();
    ImageView selected_hold;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_problem_activity);

        cancel = findViewById(R.id.cancel_button);
        next = findViewById(R.id.next_button);

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MainActivity.redirectActivity(SetProblemActivity.this, MainActivity.class);
            }
        });

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // check if the sequence is not empty
                if(!selected_holds.isEmpty()){
                    sendProblemSequence(SetProblemActivity.this, AddProblemActivity.class, Arrays.toString(selected_holds.toArray()));
                }
                else{
                    Toast.makeText(getApplicationContext(),"Select holds for the problem!" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void sendProblemSequence(Activity activity, Class aClass, String sequence) {
        // Initialize intent
        Intent intent = new Intent(activity, aClass);
        // Set Flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Problem_sequence", sequence);
        // Start the activity
        activity.startActivity(intent);
    }

    // ACTION WHEN A HOLDS IS SELECTED

    public void SelectHold(View view){
        // TODO implement drawing circles on the wall
        selected_hold = (ImageView) view;
        Toast.makeText(getApplicationContext(),"ID of the item is: "+ view.getId() , Toast.LENGTH_LONG).show();

        // Draw a circle over the selected hold

        int vWidth = view.getWidth(); // get ImageView width
        int vHeight = view.getHeight(); // get ImageView height
        /*
        if(!selected_holds.isEmpty()){

            if(selected_holds.contains(selected_hold)){
                ShapeDrawable sd = new ShapeDrawable(new OvalShape());
                sd.setIntrinsicHeight(vHeight / 2);
                sd.setIntrinsicWidth(vHeight / 2);
                sd.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                        R.color.hold_blue_background, null));;
                selected_hold.setBackground(sd);
            }
            else{
                ShapeDrawable sd = new ShapeDrawable(new OvalShape());
                sd.setIntrinsicHeight(vHeight / 2);
                sd.setIntrinsicWidth(vHeight / 2);
                sd.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                        R.color.hold_green_background, null));;
                selected_hold.setBackground(sd);
                selected_holds.add(selected_hold);
            }
        }
        else{
            ShapeDrawable sd = new ShapeDrawable(new OvalShape());
            sd.setIntrinsicHeight(vHeight / 2);
            sd.setIntrinsicWidth(vHeight / 2);
            sd.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                    R.color.hold_green_background, null));;
            selected_hold.setBackground(sd);
            selected_holds.add(selected_hold);
        }
        */
        //
        if(!selected_holds.isEmpty()){
            if(selected_holds.contains(selected_hold)){
                switch(String.valueOf(selected_hold.getTag())){
                    case "green":
                        ShapeDrawable blue = new ShapeDrawable(new OvalShape());
                        blue.setIntrinsicHeight(vHeight / 2);
                        blue.setIntrinsicWidth(vHeight / 2);
                        blue.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                                R.color.hold_blue_background, null));;
                        selected_hold.setBackground(blue);
                        selected_hold.setTag("blue");
                        break;
                    case "blue":
                        ShapeDrawable red = new ShapeDrawable(new OvalShape());
                        red.setIntrinsicHeight(vHeight / 2);
                        red.setIntrinsicWidth(vHeight / 2);
                        red.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                                R.color.hold_red_background, null));;
                        selected_hold.setBackground(red);
                        selected_hold.setTag("red");
                        break;
                    case "red":
                        ShapeDrawable empty = new ShapeDrawable(new OvalShape());
                        empty.setIntrinsicHeight(vHeight / 2);
                        empty.setIntrinsicWidth(vHeight / 2);
                        empty.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                                R.color.colorClimbingWall, null));;
                        selected_hold.setBackground(empty);
                        selected_hold.setTag("empty");
                        // remove the select_hold from the ArrayList
                        for(int i = 0; i < selected_holds.size(); i++){
                            if(selected_holds.get(i) == selected_hold){
                                selected_holds.remove(i);
                                break;
                            }
                        }
                        break;

                }
            }
            // Add new holds to the ArrayList
            else{
                ShapeDrawable next = new ShapeDrawable(new OvalShape());
                next.setIntrinsicHeight(vHeight / 2);
                next.setIntrinsicWidth(vHeight / 2);
                next.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                        R.color.hold_green_background, null));;
                selected_hold.setBackground(next);
                selected_hold.setTag("green");
                selected_holds.add(selected_hold);
            }
        }
        // Add the first hold to the ArrayList
        else{
            ShapeDrawable first = new ShapeDrawable(new OvalShape());
            first.setIntrinsicHeight(vHeight / 2);
            first.setIntrinsicWidth(vHeight / 2);
            first.getPaint().setColor(ResourcesCompat.getColor(getResources(),
                    R.color.hold_green_background, null));;
            selected_hold.setBackground(first);
            selected_hold.setTag("green");
            selected_holds.add(selected_hold);
        }

    }
}
