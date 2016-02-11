package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
         String data;
        String shareString="#SunshineApp";

        public DetailFragment() {
            setHasOptionsMenu(true);
        }
        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
            inflater.inflate(R.menu.detail_fragment, menu);
            MenuItem shareMenu=menu.findItem(R.id.share_id);
            ShareActionProvider shareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(shareMenu);
            shareActionProvider.setShareIntent(getShareForecastIntent());
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent=getActivity().getIntent();
            if(intent!=null &&intent.hasExtra(Intent.EXTRA_TEXT)) {
                 data = intent.getStringExtra(Intent.EXTRA_TEXT);
                 Log.v("DATA INtent", data);
                TextView textView = (TextView) rootView.findViewById(R.id.detailText);
                textView.setText(data);
            }
            return rootView;
        }
        public Intent getShareForecastIntent ()
        {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareString=data+shareString;
            shareIntent.putExtra(Intent.EXTRA_TEXT,shareString );
                return shareIntent;
        }
    }
}