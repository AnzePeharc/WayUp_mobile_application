package com.example.wayup_mobile_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

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
                        startActivity(new Intent(getApplicationContext(), AddProblem.class));
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
}
