package app.amoenus.traktroulette;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = ChooseLayoutType(cursor);

        View view =  LayoutInflater.from(context).inflate(layoutId, parent, false);
        ForecastListItemViewHolder viewHolder = new ForecastListItemViewHolder(view);
        view.setTag(viewHolder);
        return view;

    }

    private int ChooseLayoutType(Cursor cursor)
    {
        int viewType = getItemViewType(cursor.getPosition());

        return GetLayoutIdFromViewType(viewType);

    }

    private int GetLayoutIdFromViewType(int viewType)
    {
        int layoutId = -1;

        switch (viewType)
        {
            case VIEW_TYPE_TODAY:
            {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY:
            {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }
        return layoutId;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //ForecastListItemViewHolder already contains the references to the relevant views,
        //so set values through the viewHolder references instead of costly findViewById calls
        ForecastListItemViewHolder viewHolder = (ForecastListItemViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }
}