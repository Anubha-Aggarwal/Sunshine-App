package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends android.support.v7.app.ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
        Log.v("OnCreate","Activity Created");
    }
    @Override
    protected  void onStart()
    {
        super.onStart();
        Log.v("OnStart", "Activity Started");
    }
    @Override
    protected  void  onResume()
    {
        super.onResume();
        Log.v("On Resume", "Activity Resumed");
    }
    @Override
    protected void  onPause()
    {
        super.onPause();
        Log.v("On Paused", "Activity Paused");
    }
    @Override
    protected  void onStop()
    {
        super.onStop();
        Log.v("On Stop", "Activity Stopped");
    }
    @Override
    protected  void onDestroy()
    {
        super.onDestroy();
        Log.v("On Destroy", "Activity Destroyed");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("On Restart", "Activity Restarted");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
