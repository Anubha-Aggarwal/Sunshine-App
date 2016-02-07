package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * A forecast fragment containing a simple view.
 */
public  class ForecastFragment extends Fragment {
    private FetchWeatherTask task=new FetchWeatherTask();
    private  ArrayAdapter<String> adp;
    private List<String> list;
    public ForecastFragment() {
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
        String[] data={ "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"};
        list=new ArrayList<String>(Arrays.asList(data));
        list.add("Today-Sunny-88/63");
         adp=new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,list);
        ListView view=(ListView)rootView.findViewById(R.id.listview_forecast);
        view.setAdapter(adp);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text=adp.getItem(i);
                Toast.makeText(getActivity(),text,Toast.LENGTH_LONG).show();
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

            task.execute("182320");
           // task.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String[]>
    {
        private final String LOG_TAG=FetchWeatherTask.class.getSimpleName();
        private String formatHighandLow(double high,double low)
        {
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
            JSONObject jsonObject=new JSONObject(forecastJsonString);
            dayTime=new Time();
            String []result=new String[noOfDays];
            for(int i=0;i<noOfDays;i++)
            {
                long dateTime=dayTime.setJulianDay(julianStartDate+i);
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