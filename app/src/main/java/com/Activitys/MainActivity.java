package com.Activitys;


import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.SlidingTabs.SlidingTabLayout;
import com.adapter.TabFragmentPagerAdapter;
import com.fragments.TaskManagement.AllTaskListFragment;
import com.fragments.TaskManagement.ProjectListFragment;
import com.fragments.EfficiencyTools.TomatoListFragment;
import com.fragments.EfficiencyTools.HabitListFragment;
import com.fragments.TaskManagement.StatisticsFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;

import huti.material.R;

public class MainActivity extends ActionBarActivity implements AllTaskListFragment.OnFragmentInteractionListener
        , ProjectListFragment.OnFragmentInteractionListener
        , TomatoListFragment.OnFragmentInteractionListener
        , HabitListFragment.OnFragmentInteractionListener
        , StatisticsFragment.OnFragmentInteractionListener{

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    SlidingTabLayout mSlidingTabLayout;
    ViewPager mViewPager,mViewPager2;
    ArrayList<Fragment> mFragmentList;
    ArrayList<Fragment> mFragmentList2;
    TabFragmentPagerAdapter adapter , adapter2;
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.taskmanagement);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorMainDark));
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager2 = (ViewPager) findViewById(R.id.view_pager2);
        mViewPager2.setOffscreenPageLimit(2);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);



        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);


        // use own style rules for tab layout
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.tab_indicator_color));
        mSlidingTabLayout.setDistributeEvenly(true);





        mFragmentList = new ArrayList<Fragment>();

        AllTaskListFragment mAllTaskListFragment = AllTaskListFragment.newInstance("param1", "param2");
        ProjectListFragment mProjectListFragment = ProjectListFragment.newInstance("param1", "param2");
        StatisticsFragment mStatisticsFragment = StatisticsFragment.newInstance("param1", "param2");
        mFragmentList.add(mAllTaskListFragment);
        mFragmentList.add(mProjectListFragment);
        //mFragmentList.add(mStatisticsFragment);

        mFragmentList2 = new ArrayList<Fragment>();
        TomatoListFragment mTomatoListFragment = TomatoListFragment.newInstance("param1", "param2");
        HabitListFragment mHabitListFragment = HabitListFragment.newInstance("param1", "param2");
        mFragmentList2.add(mTomatoListFragment);
        mFragmentList2.add(mHabitListFragment);


        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, 1);
        adapter2 = new TabFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList2, 2);
        mViewPager.setAdapter(adapter);
        mViewPager2.setAdapter(adapter2);
        mViewPager2.setVisibility(View.GONE);


        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);


        // Tab events
        if (mSlidingTabLayout != null) {
            mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset,
                                           int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                   // Toast.makeText(getApplicationContext(),"positon "+position,Toast.LENGTH_SHORT)
                           // .show();

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        // Click events for Navigation Drawer
        LinearLayout navButton = (LinearLayout) findViewById(R.id.txtNavButton);
        LinearLayout navButton2 = (LinearLayout) findViewById(R.id.txtNavButton2);
        navButton.setOnClickListener(new MyOnClickListener());
        navButton2.setOnClickListener(new MyOnClickListener());

    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.txtNavButton:
                    // close drawer if you want
                    if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
                        mDrawerLayout.closeDrawers();
                    }
                    mViewPager.setVisibility(View.VISIBLE);
                    mViewPager2.setVisibility(View.GONE);
                    mSlidingTabLayout.setViewPager(mViewPager);
                    mViewPager.setCurrentItem(0);
                    mToolbar.setTitle(R.string.taskmanagement);
                    mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            //Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    break;

                case R.id.txtNavButton2:
                    // close drawer if you want
                    if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
                        mDrawerLayout.closeDrawers();
                    }
                    mViewPager2.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.GONE);
                    mSlidingTabLayout.setViewPager(mViewPager2);
                    mViewPager2.setCurrentItem(0);
                    mToolbar.setTitle(R.string.efficiencytools);
                    mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                           //Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    break;

            }



        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            MainActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



}
