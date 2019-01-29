/*
 * Copyright (C) 2015 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.apidemo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * {@link MainActivity} is the single activity for the application.  It contains a single
 * {@link NavigationDrawerFragment} in its drawer, and swaps demo fragments in and out of its
 * {@link android.widget.FrameLayout} as appropriate.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String LOG_TAG = "APIDemos";

    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();

        switch (position) {
            case 0: trans.replace(R.id.container, new AdMobAdListenerFragment()); break;
            case 1: trans.replace(R.id.container, new AdMobAdTargetingFragment()); break;
            case 2: trans.replace(R.id.container, new AdMobBannerSizesFragment()); break;
            case 3: trans.replace(R.id.container, new AdMobCustomMuteThisAdFragment()); break;
            case 4: trans.replace(R.id.container, new DFPFluidSizeFragment()); break;
            case 5: trans.replace(R.id.container, new DFPPPIDFragment()); break;
            case 6: trans.replace(R.id.container, new DFPCustomTargetingFragment()); break;
            case 7: trans.replace(R.id.container, new DFPCategoryExclusionFragment()); break;
            case 8: trans.replace(R.id.container, new DFPMultipleAdSizesFragment()); break;
            case 9: trans.replace(R.id.container, new DFPAppEventsFragment()); break;
            case 10: trans.replace(R.id.container, new DFPCustomControlsFragment()); break;
        }

        trans.commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
