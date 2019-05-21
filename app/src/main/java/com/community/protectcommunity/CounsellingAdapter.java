package com.community.protectcommunity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CounsellingAdapter extends ArrayAdapter<Counselling> {
    // item layout id
    private int resourceId;

    public CounsellingAdapter(Context context,         // context
                              int textViewResourceId,  // item layout id
                              List<Counselling> objects){    // list
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get current fact object
        Counselling counselling = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null){
            // inflate item
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();
            viewHolder.counsellingName = (TextView) view.findViewById(R.id.counselling_name);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            // get cache
            viewHolder = (ViewHolder) view.getTag();
        }

        // set name & specialty
        viewHolder.counsellingName.setText(counselling.getName());
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.oh_whale);
        viewHolder.counsellingName.setTypeface(typeface);
        return view;
    }

    class ViewHolder{
        TextView counsellingName;
    }
}

