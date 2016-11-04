package pham.ntu.grabtheater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListAdapter;

import java.util.HashMap;
import java.util.List;

import pham.ntu.grabtheater.adapter.CustomExpandableListAdapter;

/**
 * Created by Pham on 4/11/2016.
 */

public class ReviewActivity extends AppCompatActivity {

    public static ExpandableListAdapter mExpandableListAdapter;
    android.widget.ExpandableListView expandableListView;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        expandableListView = (android.widget.ExpandableListView) findViewById(R.id.expandableListView);
        mExpandableListAdapter = new CustomExpandableListAdapter(this, DetailFragment.reviewsList);
        expandableListView.setAdapter(mExpandableListAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

}

