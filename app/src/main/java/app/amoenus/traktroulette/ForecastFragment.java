package app.amoenus.traktroulette;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> mForecastAdapter;
    ListView mListView;
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Handle action bar item clicks here. Action bar will
        //automatically handle clicks on Home/Up button, so long
        //as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            UpdateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
        String location = GetDefaultSharedLocationPreference();
        weatherTask.execute(location);
    }

    private String GetDefaultSharedLocationPreference() {
        return Utility.getPreferredLocation(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        UpdateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        CreateArrayAdapter();
        SetForecastAdapterToView(rootView);
        SetOnItemClickListener();
        return rootView;
    }

    private void SetOnItemClickListener()
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id)
             {
                String forecast = mForecastAdapter.getItem(position);
                 Context context = getActivity();
                 Intent openDetailedViewIntent = new Intent(context, DetailActivity.class)
                         .putExtra(Intent.EXTRA_TEXT, forecast);
                 startActivity(openDetailedViewIntent);

             }
        }
        );
    }


    private void SetForecastAdapterToView(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);
    }

    private void CreateArrayAdapter() {
        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview, new ArrayList<String>());
    }


}
