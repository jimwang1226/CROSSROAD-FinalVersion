package com.community.protectcommunity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HospitalAdapter extends ArrayAdapter<Hospital> {
    // item layout id
    private int resourceId;

    public HospitalAdapter(Context context,         // context
                           int textViewResourceId,  // item layout id
                           List<Hospital> objects){    // list
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get current fact object
        Hospital hospital = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null){
            // inflate item
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();
            viewHolder.hospitalName = (TextView) view.findViewById(R.id.hospital_name);
            viewHolder.hospitalSpecialty = (TextView) view.findViewById(R.id.hospital_specialty);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            // get cache
            viewHolder = (ViewHolder) view.getTag();
        }

        // set name & specialty
        viewHolder.hospitalName.setText(hospital.getName());
        viewHolder.hospitalSpecialty.setText(hospital.getSpecialty());
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.oh_whale);
        viewHolder.hospitalSpecialty.setTypeface(typeface);
        viewHolder.hospitalName.setTypeface(typeface);
        return view;
    }

    class ViewHolder{
        TextView hospitalName;
        TextView hospitalSpecialty;
    }
}

