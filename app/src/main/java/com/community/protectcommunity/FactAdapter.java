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

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class FactAdapter extends ArrayAdapter<Fact> {
    // item layout id
    private int resourceId;

    public FactAdapter(Context context,         // context
                       int textViewResourceId,  // item layout id
                       List<Fact> objects){    // list
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get current fact object
        Fact fact = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null){
            // inflate item
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();
            viewHolder.factImage = (ImageView) view.findViewById(R.id.fact_image);
            viewHolder.factName = (TextView) view.findViewById(R.id.fact_name);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            // get cache
            viewHolder = (ViewHolder) view.getTag();
        }

        // set image resource
        viewHolder.factImage.setImageResource(fact.getImageId());
        // set text resource
        viewHolder.factName.setText(fact.getName());
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.oh_whale);
        viewHolder.factName.setTypeface(typeface);
        return view;
    }

    class ViewHolder{
        ImageView factImage;
        TextView factName;
    }
}

