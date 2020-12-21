package com.example.wayup_mobile_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import java.util.Arrays;
import java.util.List;

public class FilterActivity extends Activity {

    RangeSlider grade_slider;
    String[] grades = {"5a", "5a+", "5b", "5b+", "5c", "5c+", "6a", "6a+", "6b", "6b+", "6c", "6c+",
            "7a", "7a+", "7b", "7b+", "7c", "7c+", "8a", "8a+", "8b", "8b+", "8c", "8c+"};
    TextView slider_info;
    CheckBox ascending;
    CheckBox descending;
    Float[] array = {(float) 0, (float) 23};
    List<Float> slider_values = Arrays.asList(array);;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);

        // initialize variable for checkbox and add onchange listener. This makes sure that both checkboxes can't be ticked at once
        ascending = (CheckBox) findViewById(R.id.ascending);
        ascending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    descending.setChecked(false);
                }
            }
        });
        // initialize variable for checkbox and add onchange listener. This makes sure that both checkboxes can't be ticked at once
        descending = (CheckBox) findViewById(R.id.descending);
        descending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ascending.setChecked(false);
                }
            }
        });

        slider_info = (TextView) findViewById(R.id.slider_info);
        grade_slider = (RangeSlider) findViewById(R.id.grade_slider);

        grade_slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                slider_values = grade_slider.getValues();
                String minValue = grades[Math.round(slider_values.get(0))];
                String maxValue = grades[Math.round(slider_values.get(1))];
                slider_info.setText(getString(R.string.slider_info, minValue, maxValue));
            }
        });
        grade_slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return grades[(int) value];
            }
        });
    }

    public void ClickBack(View view){
        MainActivity.redirectActivity(FilterActivity.this, DatabaseActivity.class);
    }
    public void ApplyFilters(View view){

        String sortType = "";

        if(ascending.isChecked()){
            sortType = "ascending";
        }
        else{
            sortType = "descending";

        }
        // Initialize intent
        Intent intent = new Intent(FilterActivity.this, DatabaseActivity.class);
        // Set Flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sortType", sortType);
        intent.putExtra("minGrade", grades[Math.round(slider_values.get(0))]);
        intent.putExtra("maxGrade", grades[Math.round(slider_values.get(1))]);
        // Start the activity
        FilterActivity.this.startActivity(intent);
    }
}
