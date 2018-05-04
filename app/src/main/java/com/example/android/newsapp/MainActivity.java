package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<NewsStory>>{

    public static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for news story data from The Guardian
     * */
    private static final String NEWS_STORY_URL =
            "http://content.guardianapis.com/search";

    /** Adapter for the list of news stories */
    private NewsStoryAdapter mAdapter;

    /**
     * Constant value for the story loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int STORY_LOADER_ID = 1;

    /**
     * Constant progress bar.
     * This enables manipulation when or if data has been loaded.
     */
    private ProgressBar loadingBar;

    /**
     * Constant empty list textview.
     * This enables manipulation when or if data has been loaded.
     */
    private TextView emptylistTextView;

    /**
     * variable boolean to be used when not connected.
     * This enables manipulation if an internet
     * connection is available or not.
     */
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView newsStoryListView = findViewById(R.id.list);

        // Find a reference to the progressbar in the layout so it can be manipulated later
        loadingBar = findViewById(R.id.progressBar);

        // Find a reference to the empty textview for when there is no
        // data to be shown in the listview
        emptylistTextView = findViewById(R.id.emptyListTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new NewsStoryAdapter(this, new ArrayList<NewsStory>());

        // Check network connectivity & assign its value (true of false)
        // to a boolean variable
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsStoryListView.setAdapter(mAdapter);
        newsStoryListView.setEmptyView(emptylistTextView);

        // Set an item click listener on the ListView, which sends an intent
        // to a web browser to open a website with more information
        // about the selected news story.
        newsStoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news story that was clicked on
                NewsStory currentStory = mAdapter.getItem(position);

                // Convert the String URL into a URI object
                // (to pass into the Intent constructor)
                Uri newsStoryUri = Uri.parse(currentStory.getWebUrl());

                // Create a new intent to view the news story
                // via the website of The Guardian
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsStoryUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the LoaderManager,
        // in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above
        // and pass in null for the bundle. Pass in this activity for the
        // LoaderCallbacks parameter (which is valid because this activity
        // implements the LoaderCallbacks interface).
        loaderManager.initLoader(STORY_LOADER_ID, null, this);
        Log.d(LOG_TAG, "loaderManager initialised");
    }

    @Override
    // This method initializes the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options menu we specified in the XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method passes the Menu item that is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        // Assign which item was selected to a variable called "id"
        // There is only one item available here with the id "action_settings" defined
        // in the XML
        int id = item.getItemId();
        // Match the id against known menu items
        // to open the SettingsActivity with an intent
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<NewsStory>> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "loader created through onCreateLoader");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences
        // The second parameter is the default value for this preference
        String storiesPerPage = sharedPrefs.getString(
                getString(R.string.settings_stories_per_page_key),
                getString(R.string.settings_stories_per_page_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // Generate a base URI to start from
        Uri baseUri = Uri.parse(NEWS_STORY_URL);

        // the buildUpon method prepares the baseUri that we just parsed so
        // we can add query parameters to it in order to create the full URL
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameters and its values
        uriBuilder.appendQueryParameter(getString(R.string.page_size),storiesPerPage);
        uriBuilder.appendQueryParameter(getString(R.string.show_fields), getString(R.string.byline_thumbnail)); // "show-fields","byline,thumbnail"
        uriBuilder.appendQueryParameter(getString(R.string.order_by),orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.api_key),getString(R.string.the_guardian_api_key));

        // Full url for the storyloader
        String fullUrl = uriBuilder.toString();

        Log.d("fullUrl",fullUrl);

        // Create a new loader for the given URL, created using the
        // Base url and the attributes built by the URI builder
        // http://content.guardianapis.com/search?page-size=15&api-key=41b6b1c6-e183-4cb0-bbaa-e22b766e93b0
        return new StoryLoader(this, fullUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsStory>> loader,
                               List<NewsStory> newsStories) {
        Log.d(LOG_TAG, "loader finished through onLoadFinished");

        // Clear the adapter of previous news story data
        mAdapter.clear();

        // Set the appropriate text in the TextView saying either
        // that there is no internet connection or
        // that there are no stories to be shown
        if (isConnected){
            // Set empty state text to display
            // "No earthquakes found."
            emptylistTextView.setText(R.string.no_stories);
        } else {
            // Set empty state text to display
            // "No internet connection."
            emptylistTextView.setText(R.string.no_internetconnection);
        }

        // If there is a valid list of {@link NewsStory)s, then add them
        // to the adapter's data set. This will trigger the ListView to update.
        if (newsStories != null && !newsStories.isEmpty()) {
            mAdapter.addAll(newsStories);
        }

        // Kill the progressbar as the data has already tried to load
        killProgressbar();
    }

    @Override
    public void onLoaderReset(Loader<List<NewsStory>> loader) {
        Log.d(LOG_TAG, "loader being reset through onLoadReset");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    /** Method to kill the progressbar**/
    private void killProgressbar(){
        loadingBar.setVisibility(View.GONE);
    }
}
