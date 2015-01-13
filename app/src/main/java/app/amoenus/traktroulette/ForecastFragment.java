package app.amoenus.traktroulette;

import app.amoenus.traktroulette.data.WeatherContract;
import app.amoenus.traktroulette.data.WeatherContract.LocationEntry;
import app.amoenus.traktroulette.data.WeatherContract.WeatherEntry;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FORECAST_LOADER = 0;
    private String mLocation;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;

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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
