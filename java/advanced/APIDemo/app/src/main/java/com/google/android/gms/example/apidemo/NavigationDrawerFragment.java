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

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * The {@link NavigationDrawerFragment} contains a horizontal {@link android.widget.ListView}
 * displaying the names of available demo fragments.  It's placed in the drawer of the app's {@link
 * com.google.android.gms.example.apidemo.MainActivity}.
 */
public class NavigationDrawerFragment extends Fragment {

  private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
  private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

  private NavigationDrawerCallbacks callbacks;
  private ActionBarDrawerToggle drawerToggle;

  private DrawerLayout drawerLayout;
  private ListView drawerListView;
  private View fragmentContainerView;

  private int currentSelectedPosition = 0;
  private boolean fromSavedInstanceState;
  private boolean userLearnedDrawer;

  public NavigationDrawerFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Read in the flag indicating whether or not the user has demonstrated awareness of the
    // drawer. See PREF_USER_LEARNED_DRAWER for details.
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

    if (savedInstanceState != null) {
      currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
      fromSavedInstanceState = true;
    }

    // Select either the default item (0) or the last selected item.
    selectItem(currentSelectedPosition);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // Indicate that this fragment would like to influence the set of actions in the action bar.
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    drawerListView = (ListView) inflater.inflate(
        R.layout.fragment_navigation_drawer, container, false);
    drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
      }
    });
    drawerListView.setAdapter(new ArrayAdapter<>(
        getActionBar().getThemedContext(),
        android.R.layout.simple_list_item_1,
        android.R.id.text1,
        getResources().getStringArray(R.array.fragment_names)));
    drawerListView.setItemChecked(currentSelectedPosition, true);
    return drawerListView;
  }

  public boolean isDrawerOpen() {
    return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
  }

  /**
   * Users of this fragment must call this method to set up the navigation drawer interactions.
   *
   * @param fragmentId The android:id of this fragment in its activity's layout.
   * @param layout The DrawerLayout containing this fragment's UI.
   */
  public void setUp(int fragmentId, DrawerLayout layout) {
    fragmentContainerView = getActivity().findViewById(fragmentId);
    drawerLayout = layout;

    // set a custom shadow that overlays the main content when the drawer opens
    drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    // set up the drawer's list view with items and click listener

    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeButtonEnabled(true);

    // ActionBarDrawerToggle ties together the the proper interactions
    // between the navigation drawer and the action bar app icon.
    drawerToggle = new ActionBarDrawerToggle(
        getActivity(),                    /* host Activity */
        drawerLayout,                    /* DrawerLayout object */
        R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
        R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
    ) {
      @Override
      public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        if (!isAdded()) {
          return;
        }

        getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
      }

      @Override
      public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        if (!isAdded()) {
          return;
        }

        if (!userLearnedDrawer) {
          // The user manually opened the drawer; store this flag to prevent auto-showing
          // the navigation drawer automatically in the future.
          userLearnedDrawer = true;
          SharedPreferences sp = PreferenceManager
              .getDefaultSharedPreferences(getActivity());
          sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
        }

        getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
      }
    };

    // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
    // per the navigation drawer design guidelines.
    if (!userLearnedDrawer && !fromSavedInstanceState) {
      drawerLayout.openDrawer(fragmentContainerView);
    }

    // Defer code dependent on restoration of previous instance state.
    drawerLayout.post(new Runnable() {
      @Override
      public void run() {
        drawerToggle.syncState();
      }
    });

    drawerLayout.setDrawerListener(drawerToggle);
  }

  private void selectItem(int position) {
    currentSelectedPosition = position;
    if (drawerListView != null) {
      drawerListView.setItemChecked(position, true);
    }
    if (drawerLayout != null) {
      drawerLayout.closeDrawer(fragmentContainerView);
    }
    if (callbacks != null) {
      callbacks.onNavigationDrawerItemSelected(position);
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      callbacks = (NavigationDrawerCallbacks) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    callbacks = null;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Forward the new configuration the drawer toggle component.
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // If the drawer is open, show the global app actions in the action bar. See also
    // showGlobalContextActionBar, which controls the top-left area of the action bar.
    if (drawerLayout != null && isDrawerOpen()) {
      inflater.inflate(R.menu.global, menu);
      showGlobalContextActionBar();
    }
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * Per the navigation drawer design guidelines, updates the action bar to show the global app
   * 'context', rather than just what's in the current screen.
   */
  private void showGlobalContextActionBar() {
    ActionBar actionBar = getActionBar();
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setTitle(R.string.app_name);
  }

  private ActionBar getActionBar() {
    return ((AppCompatActivity) getActivity()).getSupportActionBar();
  }

  /**
   * Callbacks interface that all activities using this fragment must implement.
   */
  public interface NavigationDrawerCallbacks {

    /**
     * Called when an item in the navigation drawer is selected.
     */
    void onNavigationDrawerItemSelected(int position);
  }
}
