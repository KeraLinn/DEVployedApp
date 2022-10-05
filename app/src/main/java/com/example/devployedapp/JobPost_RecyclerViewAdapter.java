package com.example.devployedapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class JobPost_RecyclerViewAdapter extends RecyclerView.Adapter<JobPost_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<JobPostInformation> jobListArray;

    public JobPost_RecyclerViewAdapter(Context context, ArrayList<JobPostInformation> jobListArray) {
        this.context = context;
        this.jobListArray = jobListArray;
    }

    @NonNull
    @Override
    public JobPost_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This is where you inflate the layout (giving a look to our rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_fragment, parent, false);
        return new JobPost_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobPost_RecyclerViewAdapter.MyViewHolder holder, int position) {
        //assign values to the views we created in the recycler_view_fragment layout file
        //dependent based on the position of the recycler view
        //this also UPDATES the views with info
        holder.tvName.setText(jobListArray.get(position).getCompanyName());
        holder.tvTitle.setText(jobListArray.get(position).getJobTitle());
        holder.tvMatch.setText(jobListArray.get(position).getSkillsMatch());
        holder.imageView.setImageResource(jobListArray.get(position).getCompanyLogo());

    }

    @Override
    public int getItemCount() {
        //the recycler view just wants to know the number of items you want displayed.
        //This method assists the OnBind method
        return jobListArray.size();
    }

    //MUST be static
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //takes views from recycler_view_fragment layout file similar to an onCreate method

        //creating variables for the items on the recyclerView fragment
        ImageView imageView;
        TextView tvName, tvTitle, tvMatch;

        //initializing the variables
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.companyLogo);
            tvName = itemView.findViewById(R.id.companyName);
            tvTitle = itemView.findViewById(R.id.smallTitle);
            tvMatch = itemView.findViewById(R.id.smallSkillsMatch);
        }
    }
}