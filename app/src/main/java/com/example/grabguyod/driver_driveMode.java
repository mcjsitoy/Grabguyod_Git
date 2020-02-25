package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class driver_driveMode extends AppCompatActivity {

    DatabaseReference database_requestForm,getRequest,setStreet;
    Button bt_offDuty, bt_requestView, bt_mapView;
    DatabaseReference updateDriverStat;
    ListView listViewRequest;
    List<addRequest>  addRequestList;
    FirebaseUser user;
    String uid, driverStat = "Off Duty";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_drive_mode);

        listViewRequest = (ListView) findViewById(R.id.listView_Request);
        bt_offDuty = findViewById(R.id.button_offDuty);
        bt_requestView = findViewById(R.id.button_viewRequest);
        bt_mapView = findViewById(R.id.button_viewMap);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        updateDriverStat = FirebaseDatabase.getInstance().getReference("table_AvailableDriver");
        getRequest = FirebaseDatabase.getInstance().getReference("requestForm");
        addRequestList = new ArrayList<>();

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

    @Override
    protected void onStart() {
        super.onStart();
        getRequest.orderByChild("driver_number").equalTo(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    addRequestList.clear();
                    for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                        addRequest addRequest = requestSnapshot.getValue(addRequest.class);
                        addRequestList.add(addRequest);
                    }

                    list_layout adapter = new list_layout(driver_driveMode.this, addRequestList);
                    listViewRequest.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void updateDriverStat(){
        updateDriverStat = FirebaseDatabase.getInstance().getReference("table_AvailableDriver");
        updateDriverStat.child(uid).child("Driver_Status").setValue(driverStat);


    }
}
