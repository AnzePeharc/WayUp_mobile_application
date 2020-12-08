package com.example.wayup_mobile_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HoldAdapter extends RecyclerView.Adapter<HoldAdapter.ViewHolder> {

    public ItemClickListener mClickListener;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView hold_text;
        public ImageView hold_image;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            hold_text = (TextView) itemView.findViewById(R.id.hold_text);
            hold_image = (ImageView) itemView.findViewById(R.id.hold_image);
            hold_image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }
    // Store a member variable for the contacts
    private List<Hold> mHolds;

    // Pass in the contact array into the constructor
    public HoldAdapter(List<Hold> holds) {
        mHolds = holds;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public HoldAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.grid_view_items, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(HoldAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Hold hold = mHolds.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.hold_text;
        textView.setText(hold.getName());
        ImageView imageView = holder.hold_image;
        imageView.setBackgroundResource(hold.getImage());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mHolds.size();
    }


    // convenience method for getting data at click position
    public String getItem(int id) {
        return mHolds.get(id).getName();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}