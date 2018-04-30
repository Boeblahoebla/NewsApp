package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DTPAdmin on 30/04/2018.
 */

public class NewsStoryAdapter extends ArrayAdapter<NewsStory>{

    /**
     * Constructs a new {@link NewsStoryAdapter}.
     *
     * @param context of the app
     * @param newsStories is the list of stories,
     * which is the data source of the adapter
     */
    public NewsStoryAdapter(Context context, List<NewsStory> newsStories) {
        super(context, 0, newsStories);
    }

    /**
     * Returns a list item view that displays information about the
     * News Story at the given position in the list of News Stories.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView)
        // that we can reuse, otherwise, if convertView is null,
        // then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the news story at the given position in the list of news stories
        NewsStory currentStory = getItem(position);

        // Find the TextView with view ID storyTitle
        TextView titleTextView = listItemView.findViewById(R.id.storyTitle);
        // Assign the web title to its variable
        String storyTitle = currentStory.getWebTitle();
        // Display the web title of the current story in that TextView
        titleTextView.setText(storyTitle);

        // Find the TextView with view ID location
        TextView storySectionTextView = listItemView.findViewById(R.id.storySection);
        // Assign the section name to its variable
        String storySection = currentStory.getSectionName();
        // Display the location of the current story in that TextView
        storySectionTextView.setText(storySection);

        // Find the TextView with view ID webPublicationDate
        TextView storyDateTextView = listItemView.findViewById(R.id.storyDate);
        // Assign the story date to its variable
        String storyDate = currentStory.getWebPublicationDate();
        // Display the story Date of the current story in that TextView
        storyDateTextView.setText(storyDate);

        // Find the textView with view ID storyByline
        TextView storyBylineTextView = listItemView.findViewById(R.id.storyByline);
        // assign the story Byline to its variable
        String storyByline = currentStory.getStoryByline();
        // Display the story Byline of the current story in that TextView
        storyBylineTextView.setText(storyByline);

        // Find the Imageview with the view ID storyThumbnail
        ImageView storyThumbnailImageView = listItemView.findViewById(R.id.storyThumbnail);
        // Assign the story thumbnail bitmap to its variable
        Bitmap thumb = currentStory.getThumbnailImage();
        // place the object bitmap as the image resource of the Image View
        storyThumbnailImageView.setImageBitmap(thumb);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}


