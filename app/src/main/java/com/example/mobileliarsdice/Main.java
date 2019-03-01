package com.example.mobileliarsdice;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.example.mobileliarsdice.Fragments.Location_fragment;
import com.example.mobileliarsdice.Fragments.Chat_fragment;
import com.example.mobileliarsdice.Fragments.Settings_fragment;



/**
 * This class acts as the menu holding the location, chat and settings.
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


    public static Activity mMain;

    final public int index_chat = 0;
    final public int index_location = 1;
    final public int index_settings =2;
    public Chat_fragment chat_fragment;
    public Location_fragment location_fragment;
    public  Settings_fragment settings_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Since we are using fragments instead of activities we need to refer to the activity
        //containing  the fragments for some operations.
        mMain = Main.this;



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

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //nees to update the use profile!
                if(i==index_settings){
                    settings_fragment.restoreUserProfile();
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


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
                case index_chat:
                    chat_fragment = new Chat_fragment();
                    return chat_fragment;
                case index_location:
                    location_fragment =  new Location_fragment();
                    return location_fragment;
                case index_settings:
                    settings_fragment = new Settings_fragment();
                    return settings_fragment;
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();

    }



}



