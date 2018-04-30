package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import java.util.List;

/**
 * Created by DTPAdmin on 30/04/2018.
 */

public class StoryLoader extends AsyncTaskLoader {
    /** Tag for log messages */
    private static final String LOG_TAG = StoryLoader.class.getName();

    /** Query URL */
    private String urlString;

    /**
     * Constructs a new {@link StoryLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public StoryLoader(Context context, String url) {
        super(context);
        Log.d(LOG_TAG, "loader constructor entered");
        urlString = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsStory> loadInBackground() {
        Log.d(LOG_TAG, "loader started background thread through loadInBackground");
        // Don't perform the request if there are no URLs, or the first URL is null
        if (urlString == null) {
            return null;
        }

        // Perform the network request, parse the response,
        // and extract a list of earthquakes.
        List<NewsStory> result = QueryUtils.fetchStoryData(urlString);
        return result;
    }
}
