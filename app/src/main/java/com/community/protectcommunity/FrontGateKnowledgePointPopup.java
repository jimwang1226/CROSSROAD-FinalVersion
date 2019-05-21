package com.community.protectcommunity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import razerdp.basepopup.BasePopupWindow;

public class FrontGateKnowledgePointPopup extends BasePopupWindow{
    private View popupWindow;
    private TextView boyNumber;
    private TextView girlNumber;

    public FrontGateKnowledgePointPopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        popupWindow = createPopupById(R.layout.pop_up_window_front_gate_knowledge_point);
        boyNumber = (TextView)popupWindow.findViewById(R.id.boy_number);
        girlNumber = (TextView)popupWindow.findViewById(R.id.girl_number);
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
        final int[] data = new int[3];
        ref.child("childrennumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String female = dataSnapshot.child("1").child("Numerator").getValue().toString();
                String male = dataSnapshot.child("2").child("Numerator").getValue().toString();
                System.out.println(female);
                System.out.println(male);
                boyNumber.setText(male);
                girlNumber.setText(female);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void changeBackground() {
        Resources res = getContext().getResources(); //resource handle
        popupWindow.setBackground(res.getDrawable(R.drawable.victoria_enrollment, null));
    }
}
