package com.pharmacy.adapters;

/**
 * Created by BookMEds on 20-06-2016.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.pharmacy.R;

import java.util.ArrayList;

public class GooglePlacesSearchAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<String> mSubcategories;

    public GooglePlacesSearchAdapter(Context context, ArrayList<String> subcategories) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSubcategories = subcategories;
    }

    @Override
    public int getCount() {
        return mSubcategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mSubcategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.google_place_search_listview_row, parent, false);
            holder = new ViewHolder();
            holder.placeName = (TextView) convertView.findViewById(R.id.placename);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String subcategory = mSubcategories.get(position);
        holder.placeName.setText(subcategory);
        return convertView;
    }

    private static class ViewHolder {
        public TextView placeName;
    }
}

