package com.community.protectcommunity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class FactSchoolBullyTrendPopup extends BasePopupWindow{
    private View popupWindow;
    private LineChart lineChart;
    final private List<Double> girlRate = new ArrayList<Double>();
    final private List<Double> boyRate = new ArrayList<Double>();
    private XAxis xAxis;
    YAxis leftAxisEle;
    YAxis rightAxisEle;

    public FactSchoolBullyTrendPopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.fact_pop_up_window_school_bully_trend);
        lineChart = (LineChart) popupWindow.findViewById(R.id.chart_line);
        setLineChart();
        getDataFromMysql();
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

    private void setLineChart(){
        lineChart.setVisibility(View.VISIBLE);
        lineChart.setMaxVisibleValueCount(60);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        // for scaling and dragging:
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setHighlightPerDragEnabled(true);
        //set legend
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(13f);
        legend.setTextColor(Color.BLACK);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        //left y
        rightAxisEle = lineChart.getAxisLeft();
        //unit of left y, eleusage
        rightAxisEle.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                DecimalFormat format = new DecimalFormat("###,#00.0");
                return format.format(value) + "%";
            }
        });
        rightAxisEle.setTextColor(ColorTemplate.MATERIAL_COLORS[1]);
        rightAxisEle.setDrawGridLines(true);
        rightAxisEle.setGranularityEnabled(true);
        rightAxisEle.setTextSize(13f);

        //set x
        xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setTextSize(13f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        //set position at bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void setData(){
        //x
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0 ; i < girlRate.size() ; i++) {
            xVals.add(2006 + i + "");
        }

        //left y
        ArrayList<Entry> yValsLeft = new ArrayList<Entry>();
        for (int i = 0 ; i < girlRate.size() ; i++) {
            yValsLeft.add(new Entry(2006 + i, Float.valueOf(girlRate.get(i).toString()), girlRate.get(i)+""));
        }

        //right y
        ArrayList<Entry> yValsRight = new ArrayList<Entry>();
        for (int i = 0 ; i < girlRate.size() ; i++) {
            yValsRight.add(new Entry(2006 + i, Float.valueOf(boyRate.get(i).toString()), girlRate.get(i)+""));
        }

        LineDataSet setLeft = new LineDataSet(yValsLeft, "Girl");
        LineDataSet setRight = new LineDataSet(yValsRight, "Boy");
        //configure left y
        setLeft.setColor(ColorTemplate.MATERIAL_COLORS[1]);
        setLeft.setAxisDependency(YAxis.AxisDependency.LEFT);
        setLeft.setCircleColor(Color.BLACK);
        setLeft.setLineWidth(3f);
        setLeft.setCircleRadius(3f);
        setLeft.setFillAlpha(65);
        setLeft.setFillColor(ColorTemplate.getHoloBlue());
        setLeft.setHighLightColor(Color.rgb(244, 117, 117));
        setLeft.setDrawCircleHole(false);

        //configure right y
        setRight.setColor(ColorTemplate.MATERIAL_COLORS[2]);
        setRight.setAxisDependency(YAxis.AxisDependency.LEFT);
        setRight.setCircleColor(Color.BLACK);
        setRight.setLineWidth(3f);
        setRight.setCircleRadius(3f);
        setRight.setFillAlpha(65);
        setRight.setFillColor(Color.RED);
        setRight.setHighLightColor(Color.rgb(244, 117, 117));
        setRight.setDrawCircleHole(false);

        LineData data = new LineData(setRight, setLeft);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(12f);
        lineChart.setData(data);

        lineChart.setVisibleXRangeMaximum(31);
        lineChart.setVisibleXRangeMinimum(5);
        lineChart.moveViewToX(0);
        Looper.prepare();
        lineChart.animateXY(3000,3000);
        Looper.loop();
        lineChart.invalidate();
    }

    public void changeBackground() {
        Resources res = getContext().getResources(); //resource handle
        popupWindow.setBackground(res.getDrawable(R.drawable.male_to_female_ratio, null));
    }

    public void getDataFromMysql() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("start!!!!!!!!!");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection cn = DriverManager.getConnection(
                            "jdbc:mysql://35.244.125.144:3306/protect_community", "root",
                            "wangjinge2007");
                    String sqlFemale = "select Indicator from bully_trend where LGA='Victoria - Female'";
                    String sqlMale = "select Indicator from bully_trend where LGA='Victoria - Male'";
                    Statement st = (Statement) cn.createStatement();
                    ResultSet rs = st.executeQuery(sqlFemale);
                    System.out.println("here!!!!!" + rs.next());
                    while (rs.next()) {
                        String rate = rs.getString("Indicator");
                        System.out.println("female" + rate);
                        girlRate.add(Double.valueOf(rate));
                    }

                    rs = st.executeQuery(sqlMale);
                    while (rs.next()) {
                        String rate = rs.getString("Indicator");
                        System.out.println("male" + rate);
                        boyRate.add(Double.valueOf(rate));
                    }
                    cn.close();
                    st.close();
                    rs.close();
                    setData();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("class not found");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("connection error");
                }
            }
        }).start();
    }
}
