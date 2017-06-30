package com.community.mnahm5.clubsnade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Home");
        redirect();

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setUpDefaultFragment();
        setupNav();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_clubs) {
            setTitle("Clubs");
            fragment = ClubsFragment.newInstance("All");
        }
        else if (id == R.id.nav_home) {
            setTitle("Home");
            fragment = EventsFragment.newInstance("Home");
        }
        else if (id == R.id.nav_settings) {
            setTitle("Profile");
            fragment = ProfileFragment.newInstance(ParseUser.getCurrentUser().getObjectId());
        }
        else if (id == R.id.nav_club_admins) {
            setTitle("Clubs I Work For");
            fragment = ClubsFragment.newInstance("Admin");
        }
        else if (id == R.id.nav_logout) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_LONG).show();
                        redirect();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        // If there is a fragment change...
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupNav() {
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        final TextView etNavUsername = (TextView) header.findViewById(R.id.tvNavUsername);
        final TextView etNavEmail = (TextView) header.findViewById(R.id.tvNavEmail);

        etNavUsername.setText(ParseUser.getCurrentUser().getUsername());
        etNavEmail.setText(ParseUser.getCurrentUser().getEmail());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Club");
        ArrayList<String> userIds = new ArrayList<String>();
        userIds.add(ParseUser.getCurrentUser().getObjectId());
        query.whereContainedIn("admins", userIds);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0) {
                    navigationView.getMenu().findItem(R.id.nav_club_admins).setVisible(true);
                }
            }
        });
    }

    private void setUpDefaultFragment() {
        // Set up home fragment
        Fragment fragment = null;

        try {
            fragment = EventsFragment.newInstance("Home");
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment, "fragment_events").commit();
    }

    private void redirect() {
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
