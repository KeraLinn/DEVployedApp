package com.example.devployedapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.devployedapp.databinding.ActivityDrawerBaseBinding;
import com.example.devployedapp.databinding.ActivityMainBinding;
import com.example.webparser.WebParser;
import com.example.webparser.data.JobListing;
import com.example.webparser.events.handlers.ListingAddedEventHandler;
import com.example.webparser.events.handlers.SearchCompletedEventHandler;
import com.example.webparser.events.interfaces.ListingAddedCallback;
import com.example.webparser.events.interfaces.SearchCompletedCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends DrawerBaseActivity implements ListingAddedCallback, SearchCompletedCallback {

    ActivityMainBinding activityMainBinding;

    //region Variables called before OnCreate() method

    // Variables For Job Swipe Cards
    private swipeCardsArrayAdapter arrayAdapter;
    List<JobListing> rowItems;

    Dialog filtersDialog; // For filters popup window on main activity

    // Variables For WebParser
    WebParser webparser;
    ListingAddedEventHandler<MainActivity> listingAddedEventHandler;
    SearchCompletedEventHandler<MainActivity> searchCompletedEventHandler;

    DBManager dbManager;

//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        allocateActivityTitle("Job Matches");

        webparser = new WebParser();
        listingAddedEventHandler = new ListingAddedEventHandler(this);
        searchCompletedEventHandler = new SearchCompletedEventHandler(this);
        webparser.eventManager.RegisterEventHandler(listingAddedEventHandler);
        webparser.eventManager.RegisterEventHandler(searchCompletedEventHandler);

        dbManager = new DBManager(this);

        for (int i = 0; i < 10; i++) {
            if(webparser.GetJobListing() != null) {
                dbManager.insert(webparser.GetJobListing());
            }
        }

        //region SwipeCards: Initialize and Parse
        rowItems = new ArrayList<>();
        String[] companyNames = getResources().getStringArray(R.array.company_names);
        for (int i = 0; i < 5; i++){
            rowItems.add(dbManager.getNextUnseenJobListing());
        }
        //endregion

        arrayAdapter = new swipeCardsArrayAdapter(this, R.layout.swipecards_item, rowItems );
        //arrayAdapter.notifyDataSetChanged(); must be called after adding new items to the 'rowItems' list from this point

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                //this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

    //region SwipeCards: After Swiping Functions
            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(MainActivity.this, "Reject!", Toast.LENGTH_SHORT).show();
                JobListing job = (JobListing) dataObject;
                dbManager.updateJobStatus(job.GetJobID(), dbManager.REJECTED);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(MainActivity.this, "Apply!", Toast.LENGTH_SHORT).show();
                JobListing job = (JobListing) dataObject;
                dbManager.updateJobStatus(job.GetJobID(), dbManager.SAVED);
            }
//endregion

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here

                // TESTING PURPOSES
                if(webparser.GetJobListing() != null) {
                    dbManager.insert(webparser.GetJobListing());
                    rowItems.add(dbManager.getNextUnseenJobListing());
                    arrayAdapter.notifyDataSetChanged();
                    Log.d("LIST", "notified");
                } else { Log.d("LIST", "not changed");}
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                //View view = flingContainer.getSelectedView();
                //view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                //view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                // potentially link to job application or job posting on company website?
                // ^^ OR pull up a popup window with more specific, detailed information
            }
        });

        //Menu to go to profile page
        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener((View v) -> startActivity(new Intent(MainActivity.this, ProfilePage.class)));

        //Menu button to go to saved jobs page
        Button savedJobsButton = findViewById(R.id.Saved_Jobs_button);
        savedJobsButton.setOnClickListener((View view) -> startActivity(new Intent(MainActivity.this, SavedJobsListPage.class)));

        //Menu button to go to saved jobs page
        Button rejectedJobsButton = findViewById(R.id.rejected_Jobs_button);
        rejectedJobsButton.setOnClickListener((View v) -> startActivity(new Intent(MainActivity.this, RejectedJobsListPage.class)));

        filtersDialog = new Dialog(this); // For filters popup window on main activity
    }

    // For filters popup window on main activity
    public void ShowFiltersPopup(View v){
        // popup is dismissed when user clicks the completed button
        FloatingActionButton completedButton;
        filtersDialog.setContentView(R.layout.popup_filters);
        completedButton = filtersDialog.findViewById(R.id.floatingActionButton_complete);
        completedButton.setOnClickListener((View view) -> filtersDialog.dismiss());
        filtersDialog.show();
    }

    @Override
    public void ListingWasAdded() {

    }

    @Override
    public void ListingWasAdded(JobListing listing) {

    }

    @Override
    public void SearchHasCompleted() {

    }

    // For SwipeCards
    /*
    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.right)
    public void right() {

         // Trigger the right event manually.

        flingContainer.getTopCardListener().selectRight();
    }
    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }
    */
}