package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A forecast fragment containing a simple view.
 */
public  class ForecastFragment extends Fragment {
    private FetchWeatherTask task;
    private ArrayAdapter<String> adp;
    private List<String> list;
    private double latitude;
    private double longitude;

    public ForecastFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        adp = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>());
        ListView view = (ListView) rootView.findViewById(R.id.listview_forecast);
        view.setAdapter(adp);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adp.getItem(i);
                Log.v("Text", text);
                //Toast.makeText(getActivity(),text,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(intent);

            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_fresh) {
            updateWeather();
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_view_preferred_loc) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String prefLoc = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_value));
            Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", prefLoc).build();
            //Log.d("prefLocation",data);
            intent.setData(geoLocation);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        task = new FetchWeatherTask(getActivity(),adp);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String postalCode = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_value));
        task.execute(postalCode);
    }

}