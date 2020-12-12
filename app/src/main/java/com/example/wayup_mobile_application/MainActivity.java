package com.example.wayup_mobile_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    ArrayList<ImageView> selected_holds = new ArrayList<>();

    // variables for drawing
    ImageView selected_hold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





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
                        return true;
                    case R.id.show_problem:
                        return true;

                    case R.id.add_problem:
                        for(int i = 0; i < selected_holds.size(); i++){
                            System.out.println(i + ". Hold is: "+ selected_holds.get(i) +" with color: " + selected_holds.get(i).getTag().toString());
                        }
                        //startActivity(new Intent(getApplicationContext(), SetProblem.class));
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
