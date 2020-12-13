package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class DatabaseActivity extends Activity {

    DrawerLayout drawerLayout;
    ScrollView problemTable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_activity);

        // set variable for drawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        // fil the table with database content
        problemTable = findViewById(R.id.problem_table);
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
        TextView textView = new TextView(context);
        textView.setText("my text");

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(textView);
    }
}
