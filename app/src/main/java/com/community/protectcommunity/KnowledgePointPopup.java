package com.community.protectcommunity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import razerdp.basepopup.BasePopupWindow;

public class KnowledgePointPopup extends BasePopupWindow{
    private View popupWindow;
    private PieChart pieChart;
    private TextView boyRate;
    private TextView girlRate;
    private String[] chartParties = new String[]{"Boy", "Girl"};
    private String[] result;


    public KnowledgePointPopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.pop_up_window_knowledge_point);
        pieChart = (PieChart) popupWindow.findViewById(R.id.chart_pie);
        boyRate = (TextView) popupWindow.findViewById(R.id.boy_rate);
        girlRate = (TextView) popupWindow.findViewById(R.id.girl_rate);
        getDataFromFirebase();
        return popupWindow;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }

    //close the pop up window
    public void dismissPopupLayout() {
        popupWindow = null;
        this.dismiss();
        System.gc();
    }

    private void setPieChart(String[] result){
        pieChart.setVisibility(View.VISIBLE);
        //set size
        pieChart.setHoleRadius(50f);
        //half color
        pieChart.setTransparentCircleRadius(54f);
        //whether show a circle in the pie chart
        pieChart.setDrawHoleEnabled(true);
        //whether add text in the center of pie chart
        pieChart.setDrawCenterText(true);
        //text in the center
        pieChart.setCenterText("Victoria 8 Year Old Boy and Girl Rate");
        //can rotation
        pieChart.setRotationEnabled(true);
        //set percentage
        pieChart.setUsePercentValues(true);
        setData(chartParties.length - 1, 100, result);
        //set animation
        pieChart.animateXY(1500, 1500);

        pieChart.getDescription().setEnabled(false);
        Legend le = pieChart.getLegend();
        le.setEnabled(false);
        //set the position of legend
        //le.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //le.setXEntrySpace(5f);
        //le.setYEntrySpace(7f);
    }

    private void setData(int count, float range, String[] data){
        //add name
        ArrayList<String> name = new ArrayList<String>();
        for (int i = 0 ; i < count + 1; i++) {
            name.add(chartParties[i]);
        }

        //add number
        ArrayList<Float> num = new ArrayList<Float>();
        for (int i = 0 ; i < data.length; i++){
            num.add(Float.valueOf(data[i]));
        }

        List<PieEntry> entries = new ArrayList<PieEntry>();

        for (int i = 0 ; i < count + 1 ; i++){
            entries.add(new PieEntry(num.get(i), name.get(i)));
        }
        PieDataSet set = new PieDataSet(entries, "");
        //distance between data module
        set.setSliceSpace(1f);

        //add color
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);
        set.setColors(colors);
        set.setValueTextSize(13f);
        set.setHighlightEnabled(true);
        set.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                DecimalFormat format = new DecimalFormat("###,##0.0");
                return format.format(value) + "%";
            }
        });
        PieData dataPie = new PieData(set);
        pieChart.setData(dataPie);
        pieChart.invalidate();
    }

    public void getDataFromFirebase() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        final int[] data = new int[3];
        ref.child("childrennumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String totalStr = dataSnapshot.child("0").child("Numerator").getValue().toString();
                //totalStr = totalStr.replaceAll(",", "");
                int female = Integer.valueOf(dataSnapshot.child("1").child("Numerator").getValue().toString().replaceAll(",", ""));
                data[1] = female;
                int male = Integer.valueOf(dataSnapshot.child("2").child("Numerator").getValue().toString().replaceAll(",", ""));
                data[2] = male;
                data[0] = male + female;
                //System.out.println("total number" + total);
                //System.out.println("female number" + female);
                //System.out.println("male number" + male);
                int total = male + female;
                double maleRate = (double)male / (double)total;
                double femaleRate = (double)female / (double)total;
                result = new String[] {String.valueOf(maleRate), String.valueOf(femaleRate)};
                System.out.println(maleRate);
                System.out.println(femaleRate);
                setPieChart(result);
                maleRate = maleRate * 100;
                femaleRate = femaleRate * 100;
                DecimalFormat format = new DecimalFormat("###,##0.0");
                maleRate = Double.valueOf(format.format(maleRate));
                femaleRate = Double.valueOf(format.format(femaleRate));
                boyRate.setText(maleRate + "%");
                girlRate.setText(femaleRate + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void changeBackground() {
        Resources res = getContext().getResources(); //resource handle
        popupWindow.setBackground(res.getDrawable(R.drawable.male_to_female_ratio, null));
    }
}
