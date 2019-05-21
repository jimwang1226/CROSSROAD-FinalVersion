package com.community.protectcommunity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

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

import razerdp.basepopup.BasePopupWindow;

public class FactSpeechDiffPopup extends BasePopupWindow{
    private View popupWindow;
    private TextView speechDiffRatio;

    public FactSpeechDiffPopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.fact_pop_up_window_speech_diff);
        speechDiffRatio = (TextView)popupWindow.findViewById(R.id.speech_diff_ratio);
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
                    String sqlFemale = "select Indicator from speech_diff where LGA_DESC='Victoria' and Year=2017";
                    Statement st = (Statement) cn.createStatement();
                    ResultSet rs = st.executeQuery(sqlFemale);
                    while (rs.next()) {
                        String rate = rs.getString("Indicator");
                        System.out.println("speech rate:" + rate);
                        setData(rate);
                    }
                    cn.close();
                    st.close();
                    rs.close();
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

    public void setData(String rate) {
        Double rateDouble = Double.parseDouble(rate) * 100;
        DecimalFormat df = new DecimalFormat("00");
        final String rateString = df.format(rateDouble) + "%";
        new Thread(new Runnable() {
            public void run() {
                speechDiffRatio.post(new Runnable() {
                    @Override
                    public void run() {
                        speechDiffRatio.setText(rateString);
                    }
                });
            }
        }).start();
    }
}
