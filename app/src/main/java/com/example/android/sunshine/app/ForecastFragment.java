package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A forecast fragment containing a simple view.
 */
public  class ForecastFragment extends Fragment {
    private FetchWeatherTask task;
    private  ArrayAdapter<String> adp;
    private List<String> list;
    private double latitude;
    private double longitude;
    public ForecastFragment() {
    }
    @Override
    public void onStart()
    {
        super.onStart();
        updateWeather();
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
         adp=new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,new ArrayList<String>());
        ListView view=(ListView)rootView.findViewById(R.id.listview_forecast);
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
    public void onCreateOptionsMenu(Menu menu,MenuInflater  inflater)
    {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id==R.id.action_fresh) {
            updateWeather();
            return true;
        }
        else if(id==R.id.action_settings)
        {
            Intent intent=new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.action_view_preferred_loc)
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
            String prefLoc=sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_value));
            Uri geoLocation=Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",prefLoc).build();
            //Log.d("prefLocation",data);
            intent.setData(geoLocation);
            if(intent.resolveActivity(getActivity().getPackageManager())!=null)
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateWeather()
    {
        task=new FetchWeatherTask();
        SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(getActivity());
        String postalCode=sharedPref.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_value));
        task.execute(postalCode);
    }
    public class FetchWeatherTask extends AsyncTask<String,Void,String[]>
    {
        private final String LOG_TAG=FetchWeatherTask.class.getSimpleName();
        private String formatHighandLow(double high,double low)
        {
            SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(getActivity());
            String choice=sharedPref.getString(getString(R.string.pref_unit_key),getString(R.string.pref_def_unit_value));
            if(choice.equals("Imperial"))
            {
                high=(high*9/5)+32;
                low=(low*9/5)+32;
            }
            long roundHigh=Math.round(high);
            long roundLow=Math.round(low);
            return roundHigh+"/"+roundLow;
        }
        private String getReadableDateString(long time)
        {
            SimpleDateFormat simpleDate=new SimpleDateFormat("EEE MMM dd");
            return simpleDate.format(time);
        }
        private  String[] getWeatherDataFromJson(String forecastJsonString,int noOfDays) throws JSONException
        {
            android.text.format.Time dayTime=new android.text.format.Time();
            dayTime.setToNow();
            int julianStartDate= android.text.format.Time.getJulianDay(System.currentTimeMillis(),dayTime.gmtoff);
            String min="min";
            String max="max";
            String main="main";
            String list="list";
            String temp="temp";
            String weather="weather";
            String coord="coord";
            JSONObject jsonObject=new JSONObject(forecastJsonString);
            dayTime=new Time();
            String []result=new String[noOfDays];
            for(int i=0;i<noOfDays;i++)
            {
                long dateTime = dayTime.setJulianDay(julianStartDate+i);
                String dateTimeSring= getReadableDateString(dateTime);
                JSONObject weatherObj=jsonObject.getJSONArray(list).getJSONObject(i);
                double maxTemp= weatherObj.getJSONObject(temp).getDouble(max);
                double minTemp=weatherObj.getJSONObject(temp).getDouble(min);
                String weatherDes=weatherObj.getJSONArray(weather).getJSONObject(0).getString(main);
                String formattedHighLow=formatHighandLow(maxTemp,minTemp);
                result[i]=dateTimeSring+" -"+weatherDes+" -"+formattedHighLow;
            }
            return result;
        }
        @Override
        protected String[] doInBackground(String... strings) {
            HttpURLConnection urlConnection=null;
            String forecastJsonStr=null;
            String postalCode=strings[0];
            String[]result=new String[7];
            try
            {
                String forecast_base_url="http://api.openweathermap.org/data/2.5/forecast/daily?";
                String querryParam="q";
                String mode="mode";
                String units="units";
                String cnt="cnt";
                String appid="appid";
                Uri uri =Uri.parse(forecast_base_url).buildUpon().appendQueryParameter(querryParam,postalCode)
                                .appendQueryParameter(mode,"json")
                                .appendQueryParameter(units,"metric")
                                .appendQueryParameter(cnt,"7")
                                .appendQueryParameter(appid,"f4de9f840a8bc881fb8487c3671e1fe2").build();
                URL url=new URL(uri.toString());
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream in=urlConnection.getInputStream();
                urlConnection.connect();

                if(in==null)
                    forecastJsonStr=null;
                else {
                    Scanner scan = new Scanner(in);
                    StringBuilder builder=new StringBuilder();
                    while(scan.hasNextLine())
                    {
                        builder.append(scan.nextLine()+"\n");
                    }
                    if(builder.length()==0)
                        forecastJsonStr=null;
                    else
                    {
                        forecastJsonStr=builder.toString();
                        result=getWeatherDataFromJson(forecastJsonStr,7);
                        //Log.v("ForecastJsonString",forecastJsonStr);
                        /*for(String res:result)
                            Log.v("Result",res);*/
                    }
                }

            }catch(IOException e)
            {
                Log.e(LOG_TAG, "Error", e);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally
            {
                if(urlConnection!=null)
                    urlConnection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
        adp.clear();
            adp.addAll(strings);
        }
    }
}