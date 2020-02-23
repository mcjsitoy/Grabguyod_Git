package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SMSgateway extends AppCompatActivity {

    private final static int SEND_SMS_PERMISSION_REQUEST_CODE = 143;
    private final static  int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    DatabaseReference databaseReference, queryRequest, cancelQuery;
    final List<String> childlist = new ArrayList<String>();
    final List<String> keyNamelist = new ArrayList<String>();
    final List<String> noPassengerList = new ArrayList<String>();
    final List<String> requestCodeList = new ArrayList<String>();
    final List<String> timestampReqeustList = new ArrayList<String>();
    final List<String> phoneNumberList = new ArrayList<String>();
    final List<String> safetycodeList = new ArrayList<String>();
    final List<String> getRequestCodeList = new ArrayList<String>();
    public final List<String> phoneNumber = new ArrayList<String>();
    private String tempSafetyCode, tempNumber, tempNumber2, templocation, tempNoPassenger, tempRequestCode, tempTimeStamp, temprequestCode;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
    String millisInString  = dateFormat.format(new Date());
    public int sizePhone;
    Button button, bt_on, bt_off, bt_back;
    EditText editText, editText2;
    TextView tv_Count;
    boolean isActive = false;
    int count = 0, size = 0, loopcount = 0;
    String number = "09228203042";
    String sms = "99 test";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsgateway);

        button = (Button) findViewById(R.id.bt_request);
        bt_on = (Button) findViewById(R.id.button_On);
        bt_off = (Button) findViewById(R.id.button_off);
        bt_back = (Button) findViewById(R.id.button_back);
        tv_Count = (TextView) findViewById(R.id.textView_Count);
        editText = (EditText) findViewById(R.id.tb_id);
        editText2 =(EditText) findViewById(R.id.tb_noP);


        bt_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActive = true;
                Toast.makeText(SMSgateway.this, "Gateway is On", Toast.LENGTH_SHORT).show();
                counter();
            }
        });

        bt_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActive = false;
            }
        });




        //SMS Permission
        if (ContextCompat.checkSelfPermission(SMSgateway.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SMSgateway.this,
                    Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(SMSgateway.this,
                        new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);

            } else {
                ActivityCompat.requestPermissions(SMSgateway.this,
                        new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
            }
        } else {
            //do nothing//
        }

        //Receive Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)){
                //Walay mahitabo
            }
            else {
                //Pop Permission Request
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }

        //return to Home page
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SMSgateway.this, Main3Activity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case 1:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(SMSgateway.this,
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }







    //Broadcast Function Area: Sending Messages to the Offline driver. COUNT MECHANISM

    public void counter() {
        //Checking the refresh rate of Gateway.
        count++;
        tv_Count.setText("Counter: " + count);
        broadcastSMS();
        requestCodeBroadcast();
        cancelledRequestBroadcast();
        if(isActive) {
            refresh(3000);
        }
    }

    private  void refresh(int milliseconds){
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                counter();
            }
        };

        handler.postDelayed(runnable, milliseconds);
    }

    private  void broadcastSMS(){

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        queryRequest =  FirebaseDatabase.getInstance().getReference("requestForm");


        //get The REQUEST KEY OR ID
        queryRequest.orderByChild("offline_BroadcastStatus").equalTo("Pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keyNamelist.clear();
                childlist.clear();
                noPassengerList.clear();
                requestCodeList.clear();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String keyName = areaSnapshot.getKey();
                    keyNamelist.add(keyName);
                    templocation = areaSnapshot.child("location").getValue(String.class);
                    childlist.add(templocation);
                    tempNoPassenger = areaSnapshot.child("user_noP").getValue(String.class);
                    noPassengerList.add(tempNoPassenger);
                    tempRequestCode = areaSnapshot.child("requestCode").getValue(String.class);
                    requestCodeList.add(tempRequestCode);
                    tempTimeStamp = areaSnapshot.child("timeStamp").getValue(String.class);
                    timestampReqeustList.add(tempTimeStamp);
                    size = keyNamelist.size();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference.child("offlineDrivers").addValueEventListener(new ValueEventListener() {
            //Getting The Phone Number of Offline Drivers
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phoneNumber.clear();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("tb_PhoneNumber").getValue(String.class);
                    phoneNumber.add(areaName);
                    sizePhone = phoneNumber.size();
                }
                for (String phone : phoneNumber) {
                    tempNumber = phone;
                    for (int y = 0; y < size; y++) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(tempNumber, null, "Location: " + childlist.get(y) + " | Number of Riders: " + noPassengerList.get(y) + " | Request Code: " + requestCodeList.get(y) + " | Time of Request: " + timestampReqeustList.get(y), null, null);
                            queryRequest.child(keyNamelist.get(y)).child("offline_BroadcastStatus").setValue("Broadcast A");
                            Toast.makeText(SMSgateway.this, "Sent", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {/*
                            Toast.makeText(SMSgateway.this, "Failed:", Toast.LENGTH_SHORT).show();*/
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void requestCodeBroadcast(){
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        queryRequest =  FirebaseDatabase.getInstance().getReference("requestForm");

        //get The REQUEST KEY OR ID
        queryRequest.orderByChild("request_Status").equalTo("Accepted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keyNamelist.clear();
                childlist.clear();
                safetycodeList.clear();
                phoneNumberList.clear();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String keyName = areaSnapshot.getKey();
                    keyNamelist.add(keyName);
                    templocation = areaSnapshot.child("location").getValue(String.class);
                    childlist.add(templocation);
                    tempSafetyCode = areaSnapshot.child("safety_Code").getValue(String.class);
                    safetycodeList.add(tempSafetyCode);
                    tempNumber2 = areaSnapshot.child("driver_number").getValue(String.class);
                    phoneNumberList.add(tempNumber2);
                    size = keyNamelist.size();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for (int y = 0; y < size; y++) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumberList.get(y), null,  "Passenger Code: " + safetycodeList.get(y), null, null);
                queryRequest.child(keyNamelist.get(y)).child("request_Status").setValue("Accepted Code Sent");
                Toast.makeText(SMSgateway.this, "Sent", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {/*
                            Toast.makeText(SMSgateway.this, "Failed:", Toast.LENGTH_SHORT).show();*/
            }
        }


    }


    private void cancelledRequestBroadcast(){
        queryRequest =  FirebaseDatabase.getInstance().getReference("requestForm");

        queryRequest.orderByChild("request_Status").equalTo("Cancelled").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keyNamelist.clear();
                childlist.clear();
                safetycodeList.clear();
                phoneNumberList.clear();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String keyName = areaSnapshot.getKey();
                    keyNamelist.add(keyName);
                    templocation = areaSnapshot.child("location").getValue(String.class);
                    childlist.add(templocation);
                    tempSafetyCode = areaSnapshot.child("safety_Code").getValue(String.class);
                    safetycodeList.add(tempSafetyCode);
                    tempNumber2 = areaSnapshot.child("driver_number").getValue(String.class);
                    phoneNumberList.add(tempNumber2);
                    tempRequestCode = areaSnapshot.child("requestCode").getValue(String.class);
                    requestCodeList.add(temprequestCode);
                    size = keyNamelist.size();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for (int y = 0; y < size; y++) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumberList.get(y), null,  "Request code '" + requestCodeList.get(y) +"' is cancelled", null, null);
                queryRequest.child(keyNamelist.get(y)).child("offline_BroadcastStatus").setValue("Broadcast C");
                Toast.makeText(SMSgateway.this, "Sent", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {/*
                            Toast.makeText(SMSgateway.this, "Failed:", Toast.LENGTH_SHORT).show();*/
            }
        }
    }





    /*//Message Sending Function
    public void sendText(){
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null,sms, null, null);
            Toast.makeText(SMSgateway.this, "Sent" + tempNumber, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(SMSgateway.this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }*/
}
