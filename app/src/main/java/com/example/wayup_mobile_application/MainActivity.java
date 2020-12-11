package com.example.wayup_mobile_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    ArrayList<ImageView> holds;

    // variables for drawing
    Bitmap mBitmap;
    Paint mPaint = new Paint();
    Canvas mCanvas;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPaint.setColor(ResourcesCompat.getColor(getResources(),
                R.color.colorPrimary, null));




        // Lookup the recyclerview in activity layout
        /*
        RecyclerView rvHolds = (RecyclerView) findViewById(R.id.climbing_wall);

        // Initialize contacts
        holds = Hold.createHoldsList(100);
        // Create adapter passing in the sample user data
        adapter = new HoldAdapter(holds);
        // Attach the adapter to the recyclerview to populate items
        adapter.setClickListener(this);
        rvHolds.setAdapter(adapter);
        // Set layout manager to position the items
        rvHolds.setLayoutManager(new GridLayoutManager(this, 10));
        */



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
                        startActivity(new Intent(getApplicationContext(), SetProblem.class));
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
        mImageView = (ImageView) view;
        Toast.makeText(getApplicationContext(),"ID of the item is: "+view.getWidth() , Toast.LENGTH_LONG).show();

        // Draw a circle over the selected hold

        int vWidth = view.getWidth();
        int vHeight = view.getHeight();
        int halfWidth = vWidth / 2;
        int halfHeight = vHeight / 2;

        mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
        mImageView.setImageBitmap(mBitmap);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawCircle(halfWidth, halfHeight, halfWidth / 3, mPaint);


    }
}
