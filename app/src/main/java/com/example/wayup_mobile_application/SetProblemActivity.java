package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class SetProblemActivity extends Activity{

    Button cancel;
    Button next;
    // variable for hold sequence counter
    int counter = 1;

    // variable for alert PopUp
    AlertDialog dialog;

    // variables for interactive climbing wall
    ArrayList<Integer> selected_holds = new ArrayList<>();
    ArrayList<Integer> selected_holds_counters = new ArrayList<>();
    ImageView selected_hold;
    TextView selected_hold_counter;

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
                    // convert ArrayList with TextView ids into comma separated string
                    StringBuffer sb_image = new StringBuffer();
                    StringBuffer sb_image_tags = new StringBuffer();

                    for (Integer image : selected_holds) {

                        ImageView get_tag = findViewById(image); // get ImageView of the selected hold
                        String tag = (String) get_tag.getTag(); // get tag of the ImageView so you know which color to use in MainScreen
                        // build image_tags string
                        sb_image_tags.append(tag);
                        sb_image_tags.append(",");
                        // build image ID string
                        sb_image.append(image);
                        sb_image.append(",");
                    }
                    String selected_holdsString = sb_image.toString();
                    String selected_holds_tagsString = sb_image_tags.toString();
                    System.out.println(selected_holdsString);
                    // convert ArrayList with TextView ids into comma separated string
                    StringBuffer sb_text = new StringBuffer();

                    for (Integer counter : selected_holds_counters) {
                        sb_text.append(counter);
                        sb_text.append(",");
                    }
                    String selected_holds_countersString = sb_text.toString();
                    sendProblemSequence(SetProblemActivity.this, AddProblemActivity.class,
                            selected_holdsString, selected_holds_tagsString, selected_holds_countersString);
                }
                else{
                    Toast.makeText(getApplicationContext(),"No holds selected!" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void sendProblemSequence(Activity activity, Class aClass, String sequence, String sequence_tags, String counters) {
        // Initialize intent
        Intent intent = new Intent(activity, aClass);
        // Set Flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Problem_sequence", sequence);
        intent.putExtra("Problem_sequence_tags", sequence_tags);
        intent.putExtra("Problem_sequence_counters", counters);
        // Start the activity
        activity.startActivity(intent);
    }

    // ACTION WHEN A HOLDS IS SELECTED

    public void SelectHold(View view){
        // get ID of clicked LinearLayout and use it to find its ImageView and TextView
        String[] id_split = getResources().getResourceEntryName(view.getId()).split("_");
        // get ImageView ID
        int hold_id = getResources().getIdentifier(id_split[1], "id", getPackageName());
        selected_hold = (ImageView) view.findViewById(hold_id);
        // get TextView ID
        int hold_counter_id = getResources().getIdentifier("counter_"+id_split[1], "id", getPackageName());
        selected_hold_counter = (TextView) view.findViewById(hold_counter_id);

        // Draw a circle over the selected hold

        int vWidth = view.getWidth(); // get LinearLayout width (LinearLayout wraps ImageView and TextView)
        int vHeight = view.getHeight(); // get LinearLayout height (LinearLayout wraps ImageView and TextView)

        //
        if(!selected_holds.isEmpty()){
            if(selected_holds.contains(selected_hold.getId())){
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
                            if(selected_holds.get(i) == selected_hold.getId()){
                                selected_holds.remove(i);
                                break;
                            }
                        }
                        // reset Text and Tag of the TextView and remove it from the ArrayList
                        selected_hold_counter.setText("");
                        selected_hold_counter.setTag("");
                        for(int i = 0; i < selected_holds_counters.size(); i++) {
                            if (selected_holds_counters.get(i) == selected_hold_counter.getId()) {
                                selected_holds_counters.remove(i);
                                break;
                            }
                        }
                        counter --; // decrease the counter
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
                selected_holds.add(selected_hold.getId());
                // add the selected TextView to the ArrayList - to keep track of counters
                selected_holds_counters.add(selected_hold_counter.getId());
                // set counter of the selected hold, to show the correct sequence
                selected_hold_counter.setText(Integer.toString(counter));
                // set the tag to the value of the counter, so it can be shown in the mainScreen after selected from DatabaseActivity
                selected_hold_counter.setTag(Integer.toString(counter));
                counter ++; // increase counter by 1
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
            // add the selected ImageView to the ArrayList
            selected_holds.add(selected_hold.getId());
            // add the selected TextView to the ArrayList - to keep track of counters
            selected_holds_counters.add(selected_hold_counter.getId());
            // set counter of the selected hold, to show the correct sequence
            selected_hold_counter.setText(Integer.toString(counter));
            // set the tag to the value of the counter, so it can be shown in the mainScreen after selected from DatabaseActivity
            selected_hold_counter.setTag(Integer.toString(counter));
            counter ++; // increase counter by 1
        }

    }

    // Alert dialog to show instructions for the user - Called when info ImageView is pressed
    public void UserInfo(View view){

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SetProblemActivity.this);

        builder.setTitle("There are four options when selecting a single climbing hold:");
        builder.setMessage("1. Green - Starting hold."+"\n"+"2. Blue - Middle hold."+"\n"+"3. Red - finish hold."+"\n"+"4. Deselect climbing hold.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.show();
    }
}
