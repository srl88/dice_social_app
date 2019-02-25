package com.example.mobileliarsdice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.example.mobileliarsdice.Models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;



/**
 * This class acts as the menu holding the location, messages and settings.
 * Add extra if required!
 */
public class Main extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    //USER MUST CAN BE ACCESSED  FROM ALL CLASSES... SINCE
    //THIS MAIN CLASS IS THE PIVOT POINT OF THE APP WE ADD IT HERE.
    static public  Users mUser = null;

    //Maybe we need it... maybe we don't...
    private boolean firstLaunched = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * GET THE UPDATED USER!.
         */
        String _id = FireBaseGlobals.getUser().getUid();
        DatabaseReference refUser = FireBaseGlobals.getDataBase().getReference("USERS").child(_id);
        //ANY CHANGES ON THE USER_DATABASE WILL BE REFLECTED ON THE USER OBJECT
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser =  dataSnapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO:read documentation to see the type of error
            }
        });



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        // add the tabs
        View messageTab = getLayoutInflater().inflate(R.layout.customtab, null);
        messageTab.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_message);
        tabLayout.addTab(tabLayout.newTab().setCustomView(messageTab));


        View locationTab = getLayoutInflater().inflate(R.layout.customtab, null);
        locationTab.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_people_);
        tabLayout.addTab(tabLayout.newTab().setCustomView(locationTab));


        View settingsTab = getLayoutInflater().inflate(R.layout.customtab, null);
        settingsTab.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_settings);
        tabLayout.addTab(tabLayout.newTab().setCustomView(settingsTab));


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        //Make the initial set up location (ready to play!)
        mViewPager.setCurrentItem(1);

        //GETS RID OF THE TOP BAR
        try{
            this.getSupportActionBar().hide();
        }catch(Exception e){}


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * On click on the Tab... determines what fragment (Activity) needs to be launched!
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new Message_fragment();
                case 1:
                    return new Location_fragment();
                case 2:
                    return new Settings_fragment();
                default:
                    return null;

            }
        }

        /**
         * Dont fucking know what this shit i supposed to do!
         * @return
         */
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }


}



