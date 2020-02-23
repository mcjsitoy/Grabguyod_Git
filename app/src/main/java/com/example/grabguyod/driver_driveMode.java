package com.example.grabguyod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class driver_driveMode extends AppCompatActivity {

    Button bt_offDuty, bt_requestView, bt_mapView;
    DatabaseReference updateDriverStat;
    FirebaseUser user;
    String uid, driverStat = "Off Duty";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_drive_mode);

        bt_offDuty = findViewById(R.id.button_offDuty);
        bt_requestView = findViewById(R.id.button_viewRequest);
        bt_mapView = findViewById(R.id.button_viewMap);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();


        bt_offDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDriverStat();
                Intent intent = new Intent(driver_driveMode.this, driver_landingpage.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        bt_requestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(driver_driveMode.this, driver_requestList.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }

    private void updateDriverStat(){
        updateDriverStat = FirebaseDatabase.getInstance().getReference("table_AvailableDriver");
        updateDriverStat.child(uid).child("Driver_Status").setValue(driverStat);


    }
}
