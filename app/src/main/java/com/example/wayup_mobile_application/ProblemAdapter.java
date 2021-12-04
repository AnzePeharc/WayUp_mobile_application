package com.example.wayup_mobile_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;


public class ProblemAdapter extends ArrayAdapter<Problem> implements Filterable {

    public ArrayList<Problem> originalList, tempList;
     CustomFilter cs;


    public ProblemAdapter(Context context, ArrayList<Problem> list) {
        super(context, 0 , list);
        this.originalList = list;
        this.tempList = list;
    }

    @Override
    public Problem getItem(int i){
        return originalList.get(i);
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

    @Override
    public int getCount(){
        return originalList.size();
    }

    // Override getFilter in order to implement searching by problem name
    @NonNull
    @Override
    public Filter getFilter() {
        if (cs == null){
            cs = new CustomFilter();
        }

        return cs;
    }

    class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            System.out.println(originalList.size());
            FilterResults results = new FilterResults(); // initialize results variable
            ArrayList<Problem> filteredList = new ArrayList<>(); // create empty ArrayList for the filtered problems

            // check if search term is not empty and filter the arraylist
            if (constraint != null && constraint.length() > 0){

                constraint = constraint.toString().toLowerCase().trim();

                for (Problem problem : originalList){

                    if(problem.getName().toLowerCase().contains(constraint)){
                        System.out.println(problem.getName());
                        System.out.println(constraint);
                        filteredList.add(problem);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }

            // if the search term is empty, therefore show all results
            else{
                System.out.println("Pride!");
                results.values = tempList;
                results.count = tempList.size();
            }



            return results;
        }

        // function that updates the adapter values
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            for (Problem problem : (ArrayList<Problem>) results.values){
                System.out.println(problem.getName());
            }
            originalList = (ArrayList<Problem>) results.values;
            notifyDataSetChanged(); // tell the adapter to update the values on the screen
        }
    };

}

