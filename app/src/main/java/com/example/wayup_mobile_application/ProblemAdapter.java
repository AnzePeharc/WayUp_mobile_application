package com.example.wayup_mobile_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ProblemAdapter extends ArrayAdapter<Problem> {

    public ProblemAdapter(Context context, ArrayList<Problem> list) {
        super(context, 0 , list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Problem new_problem = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.table_item,parent,false);

        TextView name = (TextView) convertView.findViewById(R.id.problem_name);
        name.setText(new_problem.getName());

        TextView grade = (TextView) convertView.findViewById(R.id.problem_grade);
        grade.setText(new_problem.getGrade());

        TextView setter = (TextView) convertView.findViewById(R.id.problem_setter);
        setter.setText(new_problem.getSetter());

        TextView comment = (TextView) convertView.findViewById(R.id.problem_comment);
        comment.setText(new_problem.getComment());

        return convertView;
    }
}
