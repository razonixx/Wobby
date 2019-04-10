package com.wobby;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class JobProviderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String name, email;
    private boolean isJobProvider = false, isJobSeeker = false;
    private Intent startIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_provider);
        startIntent = getIntent();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        View headerView = navigationView.getHeaderView(0);
        TextView providerName = headerView.findViewById(R.id.JobProviderName);
        TextView providerMail = headerView.findViewById(R.id.JobProviderMail);
        providerName.setText(startIntent.getStringExtra("USER_FIRST_NAME") + " " + startIntent.getStringExtra("USER_LAST_NAME"));
        providerMail.setText(startIntent.getStringExtra("USERNAME"));
        isJobProvider = startIntent.getBooleanExtra("ISJOBPROVIDER", false);
        isJobSeeker = startIntent.getBooleanExtra("ISJOBSEEKER", false);


        if(isJobProvider){
            try{
                navigationView.getMenu().findItem(R.id.job_new).setVisible(true);
                navigationView.getMenu().findItem(R.id.job_view).setVisible(true);
                navigationView.getMenu().findItem(R.id.job_update).setVisible(true);
                navigationView.getMenu().findItem(R.id.job_delete).setVisible(true);
            }
            catch (Exception e){
                Log.wtf("ERROR", e.toString());
            }
        }
        if(isJobSeeker){
            try{
                navigationView.getMenu().findItem(R.id.job_seeker_near).setVisible(true);
                navigationView.getMenu().findItem(R.id.job_seeker_map).setVisible(true);
            }
            catch (Exception e){
                Log.wtf("ERROR", e.toString());
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        JobViewFragment jobViewFragment = new JobViewFragment();
        transaction.add(R.id.container, jobViewFragment, "jobViewFragment");
        transaction.commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.job_view) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment f = manager.findFragmentByTag("jobEditFragment");
            Fragment f2 = manager.findFragmentByTag("jobViewFragment");
            Fragment f3 = manager.findFragmentByTag("jobDeleteFragment");
            if(f != null){
                transaction.remove(f);
            }
            if(f2 != null){
                transaction.remove(f2);
            }
            if(f3 != null){
                transaction.remove(f3);
            }
            JobViewFragment jobViewFragment = new JobViewFragment();
            transaction.add(R.id.container, jobViewFragment, "jobViewFragment");
            transaction.commit();
        }
        else if (id == R.id.job_new) {
            Intent intent = new Intent(getApplicationContext(), JobCreateActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.job_update) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment f = manager.findFragmentByTag("jobEditFragment");
            Fragment f2 = manager.findFragmentByTag("jobViewFragment");
            Fragment f3 = manager.findFragmentByTag("jobDeleteFragment");
            if(f != null){
                transaction.remove(f);
            }
            if(f2 != null){
                transaction.remove(f2);
            }
            if(f3 != null){
                transaction.remove(f3);
            }
            JobEditFragment jobEditFragment = new JobEditFragment();
            transaction.add(R.id.container, jobEditFragment, "jobEditFragment");
            transaction.commit();
        }
        else if (id == R.id.job_delete) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment f = manager.findFragmentByTag("jobEditFragment");
            Fragment f2 = manager.findFragmentByTag("jobViewFragment");
            Fragment f3 = manager.findFragmentByTag("jobDeleteFragment");
            if(f != null){
                transaction.remove(f);
            }
            if(f2 != null){
                transaction.remove(f2);
            }
            if(f3 != null){
                transaction.remove(f3);
            }
            JobDeleteFragment jobDeleteFragment = new JobDeleteFragment();
            transaction.add(R.id.container, jobDeleteFragment, "jobDeleteFragment");
            transaction.commit();
        }
        else if (id == R.id.job_seeker_near) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment f = manager.findFragmentByTag("jobEditFragment");
            Fragment f2 = manager.findFragmentByTag("jobViewFragment");
            Fragment f3 = manager.findFragmentByTag("jobDeleteFragment");
            if(f != null){
                transaction.remove(f);
            }
            if(f2 != null){
                transaction.remove(f2);
            }
            if(f3 != null){
                transaction.remove(f3);
            }
            JobSeekerViewFragment jobSeekerViewFragment = new JobSeekerViewFragment();
            transaction.add(R.id.container, jobSeekerViewFragment, "jobViewFragment");
            transaction.commit();
        }
        else if (id == R.id.job_seeker_map) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("MODE", "JOB_SEEKER");
            startActivity(intent);
        }
        else if (id == R.id.job_share) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
