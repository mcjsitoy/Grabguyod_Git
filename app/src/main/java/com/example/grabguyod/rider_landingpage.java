package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class rider_landingpage extends AppCompatActivity {

    DatabaseReference driverCount;
    String uid, tempName, tempCount, tempKey, tempStat;
    private Button bt_logout, bt_getDriver;
    final List<String> keyNamelist = new ArrayList<String>();
    final List<String> driverStat = new ArrayList<String>();
    private TextView tv_count, tv_name;
    FirebaseUser user;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_landingpage);
        tv_count = findViewById(R.id.textView_driverCount2);
        tv_name = findViewById(R.id.textView_userName2);
        bt_logout = findViewById(R.id.button_logout);
        bt_getDriver = findViewById(R.id.button_GetDriver);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        getName();
        availableDriverCount();

        bt_getDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rider_landingpage.this, requestForm.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null ) {
                    Toast.makeText(rider_landingpage.this, "Logged Out", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(rider_landingpage.this, "Not Logout", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(rider_landingpage.this, Main3Activity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }

    private void getName(){
        DatabaseReference getName = FirebaseDatabase.getInstance().getReference("users");

        getName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempName = dataSnapshot.child("riders").child(uid).child("tb_FullName").getValue(String.class);
                tv_name.setText(tempName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void availableDriverCount(){
        driverCount = FirebaseDatabase.getInstance().getReference().child("driveravailable");

        driverCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){/*
                    Double status = dataSnapshot.child("T6EJWQMMgwPLsT6pMrt9MT70Mtn2").child("l").child("1").getValue(Double.class);
                    String numberAsString = Double.toString(status);
                    Toast.makeText(requestForm.this, numberAsString,Toast.LENGTH_SHORT).show();*/

                    size = (int) dataSnapshot.getChildrenCount();
                    tempCount = Integer.toString(size);
                    tv_count.setText(tempCount);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
