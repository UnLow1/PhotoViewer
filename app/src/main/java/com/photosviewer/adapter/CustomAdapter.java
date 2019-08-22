package com.photosviewer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.photosviewer.R;
import com.photosviewer.model.Photo;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Photo> {

    private static class ViewHolder {
        private TextView photoTitleTextView;
        private ImageView photoImageView;
        private TextView photoTagsTextView;
        private TextView photoDateTakenTextView;
        private TextView photoPublishedTextView;
    }

    private ViewHolder viewHolder;

    public CustomAdapter(Context context,int textViewResourceId, List<Photo> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.listview_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.photoTitleTextView = convertView.findViewById(R.id.photoTitleTextView);
            viewHolder.photoImageView = convertView.findViewById(R.id.photoImageView);
            viewHolder.photoTagsTextView = convertView.findViewById(R.id.photoTagsTextView);
            viewHolder.photoDateTakenTextView = convertView.findViewById(R.id.photoDateTakenTextView);
            viewHolder.photoPublishedTextView = convertView.findViewById(R.id.photoPublishedTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Photo item = getItem(position);
        if (item!= null) {
            viewHolder.photoTitleTextView.setText(item.getTitle());
            viewHolder.photoTagsTextView.setText(item.getTags().toString());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss z");
            viewHolder.photoDateTakenTextView.setText(item.getDateTaken().format(formatter));
            viewHolder.photoPublishedTextView.setText(item.getPublished().format(formatter));

            URL url;
            try {
                url = new URL(item.getUrl());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                viewHolder.photoImageView.setImageBitmap(bmp);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }
}