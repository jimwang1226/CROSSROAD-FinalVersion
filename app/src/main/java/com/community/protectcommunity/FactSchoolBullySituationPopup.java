package com.community.protectcommunity;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import razerdp.basepopup.BasePopupWindow;

public class FactSchoolBullySituationPopup extends BasePopupWindow{
    private View popupWindow;
    private TextView totalNumber;
    private TextView bullyRatio;

    public FactSchoolBullySituationPopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.fact_pop_up_window_school_bully_situation);
        totalNumber = (TextView)popupWindow.findViewById(R.id.fact_total_number);
        bullyRatio = (TextView)popupWindow.findViewById(R.id.fact_bully_ratio);
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

    public void getDataFromFirebase() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("childrennumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String total = dataSnapshot.child("0").child("Numerator").getValue().toString();
                String ratio = dataSnapshot.child("0").child("Indicator").getValue().toString();
                ratio = ratio.substring(0,ratio.length()-2) + "%";
                System.out.println(total);
                System.out.println(ratio);
                totalNumber.setText(total);
                bullyRatio.setText(ratio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
