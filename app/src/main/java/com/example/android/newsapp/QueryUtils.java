package com.example.android.newsapp;

/**
 * Created by DTPAdmin on 30/04/2018.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving earthquake data from
 * the Guardian web API.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a
     * {@link QueryUtils} object. This class is only meant to hold static variables
     * and methods, which can be accessed directly from the class name QueryUtils
     * (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link NewsStory} objects that has been
     * built up from parsing the given JSON response.
     */
    private static List<NewsStory> extractFeatureFromJson(String storyJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(storyJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news stories to
        List<NewsStory> newsStories = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with
        // the way the JSON is formatted, a JSONException exception object
        // will be thrown. Catch the exception so the app doesn't crash,
        // and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(storyJSON);

            // Extract the JSON object which holds the news stories
            JSONObject JsonResponse = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of stories.
            JSONArray newsStoriesJsonArray = JsonResponse.getJSONArray("results");

            // For each story in the newsStoriesJsonArray,
            // create an {@link NewsStory} object
            for (int i = 0; i < newsStoriesJsonArray.length(); i++) {

                // Get a single news story at position i
                // within the list of news stories
                JSONObject currentStory = newsStoriesJsonArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String webTitle = currentStory.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = currentStory.getString("sectionName");

                // Extract the value for the key called "webUrl"
                String webUrl = currentStory.getString("webUrl");

                // Extract the value for the key called "webPublicationDate"
                String webPublicationDate = currentStory.getString("webPublicationDate");

                // Extract the JSON object called "fields" and
                // Extract its fields "byline" and "thumbnail"
                JSONObject storyFields = currentStory.getJSONObject("fields");
                String storyByLine = storyFields.getString("byline");
                String thumbNailUrl = storyFields.getString("thumbnail");

                // Create & download the bitmap with the use of the thumbNailUrl
                Bitmap storyThumbnail = getStoryThumbnail(thumbNailUrl);

                // Create a new {@link NewsStory} object with the web title,
                // section name, web url, publication date and thumbnail
                // from the JSON response.
                NewsStory story = new NewsStory(webTitle, sectionName, webUrl, webPublicationDate, storyByLine, storyThumbnail);

                // Add the new {@link NewsStory} to the list of news stories.
                newsStories.add(story);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements
            // in the "try" block, catch the exception here, so the app doesn't crash.
            // Print a log message with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news stories JSON results", e);
        }

        // Return the list of news stories
        return newsStories;
    }

    /**
     * Query the story dataset and return a list of {@link NewsStory} objects.
     */
    public static List<NewsStory> fetchStoryData(String requestUrl) {
        Log.d(LOG_TAG, "loader has started fetching the news stories");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        Log.d(LOG_TAG, "Json response = " +jsonResponse);

        // Extract relevant fields from the JSON response and
        // create a list of {@link NewsStory}s
        List<NewsStory> stories = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link NewsStory}s
        return stories;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " +
                        urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the newsstories JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException,
                // which is why the makeHttpRequest(URL url) method signature
                // specifies than an IOException could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Convert the thumbnail URL into a Bitma
     * so it can be shown & updated in the ImageView of the ListItem
     */
    private static Bitmap getStoryThumbnail(String url){
        Bitmap thumb = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            thumb = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return thumb;
    }
}