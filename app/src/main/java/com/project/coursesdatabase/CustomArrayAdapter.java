package com.project.coursesdatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// Adapted from https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class CustomArrayAdapter extends ArrayAdapter<FileClass> {
    public CustomArrayAdapter(Context context, ArrayList<FileClass> files) {
        super(context, 0, files);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FileClass f = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
        }
        // Lookup view for data population
        TextView doctitle = (TextView) convertView.findViewById(R.id.doctitle);
        TextView uploaddate=(TextView)  convertView.findViewById(R.id.uploaddate);
        TextView description=(TextView)  convertView.findViewById(R.id.description);

        TextView user = (TextView) convertView.findViewById(R.id.user);
        // Populate the data into the template view using the data object
        doctitle.setText(f.getName());
        uploaddate.setText(f.getUploadtime());
        user.setText(f.getUsername());
        description.setText(f.getDescc());
        // Return the completed view to render on screen
        return convertView;
    }
}