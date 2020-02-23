package com.example.grabguyod;


import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class myReceiverSMS extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static final String TAG = "SmsBroadcastReceiver";
    DatabaseReference database_smsToDatabase, q_requestForm, databaseReference,driversearch;
    public String msg, phoneNo;
    public static String TempPhone = "0" , driver_id;
    private String systemCode = "213",systemCodePick = "501", systemCodeDrop = "502", systemCodeDutyOn = "101", systemCodeDutyOff = "102",Codechecker = "",requestCode = "";
    private String keyName, id_request,id_user,noP_user,offlineBroadcastStatus,status_request, timestamp,user_location;
    int count = 0;
    @TargetApi(Build.VERSION_CODES.M)

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Intent Received: " + intent.getAction());

        if (intent.getAction()==SMS_RECEIVED){
            Bundle dataBundle = intent.getExtras();
            if(dataBundle!=null){
                Object[]pdus = (Object[])dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[pdus.length];

                for (int i = 0; i <pdus.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = dataBundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[])pdus[i],format);
                    }
                    else{
                        message[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    msg = message[i].getMessageBody();
                    phoneNo = message[i].getOriginatingAddress();
                }/*
                Toast.makeText(context, "Message: " + msg + "\nNumber: " + phoneNo, Toast.LENGTH_LONG).show();*/
                //save SMS
                checkSMS();


                Toast.makeText(context, "ID: " + driver_id + "\nNumber: " + count, Toast.LENGTH_LONG).show();

                if (Codechecker.equals(systemCode))  {
                    database_smsToDatabase = FirebaseDatabase.getInstance().getReference("table_SMS_LOG");
                    String SMS_id = database_smsToDatabase.push().getKey();
                    sms_To_Database add_sms = new sms_To_Database(SMS_id, phoneNo, msg);

                    database_smsToDatabase.child(SMS_id).setValue(add_sms);/*
                    Toast.makeText(context, "Code" + requestCode, Toast.LENGTH_LONG).show();*/

                    //get The REQUEST KEY OR ID
                    q_requestForm =  FirebaseDatabase.getInstance().getReference("requestForm");
                    q_requestForm.orderByChild("requestCode").equalTo(requestCode).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                keyName = areaSnapshot.getKey();
                                id_request = areaSnapshot.child("request_id").getValue(String.class);
                                id_user = areaSnapshot.child("user_id").getValue(String.class);
                                noP_user = areaSnapshot.child("user_noP").getValue(String.class);
                                offlineBroadcastStatus = areaSnapshot.child("offline_BroadcastStatus").getValue(String.class);
                                status_request = areaSnapshot.child("request_Status").getValue(String.class);
                                timestamp = areaSnapshot.child("timeStamp").getValue(String.class);
                                user_location = areaSnapshot.child("location").getValue(String.class);

                                waitConfirmation();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else if (Codechecker.equals(systemCodePick)){ //Code 501 Driver Picks up Rider

                } else if (Codechecker.equals(systemCodeDrop)){ //Code 502 Driver Drop Rider

                } else if (Codechecker.equals(systemCodeDutyOn)){ //Code 101 Driver Start
                    driversearch =  FirebaseDatabase.getInstance().getReference("table_AvailableDriver");
                    driversearch.child(TempPhone).child("Driver_Status").setValue("Available");
                } else if (Codechecker.equals(systemCodeDutyOff)){ //Code 102 Driver Off Duty
                    driversearch =  FirebaseDatabase.getInstance().getReference("table_AvailableDriver");
                    driversearch.child(TempPhone).child("Driver_Status").setValue("Off Duty");
                } else {
                    Toast.makeText(context, "SMS not part of the System.", Toast.LENGTH_LONG).show();
                }


            }
        }

    }



    //Checks if message is part of the SYSTEM
    public void checkSMS() {
        Codechecker = "";
        requestCode = "";
        TempPhone = "0";
        char[] ch = new char[msg.length()];
        char[] ph = new char[phoneNo.length()];
        //SMS to Char
        for (int i = 0; i < msg.length(); i++) {
            ch[i] = msg.charAt(i);
        }
        for (int i = 0; i < 3; i++) {
            Codechecker += ch[i];
        }
        if (msg.length() == 4) {
            for (int i = 4; i < 8; i++) {
                requestCode += ch[i];
            }
        }
        for (int i = 0; i < phoneNo.length(); i++) {
            ph[i] = phoneNo.charAt(i);
        }
        for (int i = 3; i < 13; i++){
            TempPhone += ph[i];

        }
    }


    //Checks The Message if Taken or not. If not it will register Driver to the request.
    private void waitConfirmation(){

        if (!status_request.equals("Pending") && !status_request.equals("Cancelled")){
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null,  "Request Already Taken" + status_request, null, null);
            } catch (Exception e){

            }

        } else if (status_request.equals("Pending")) {
            q_requestForm.child(keyName).child("request_Status").setValue("Waiting Confirmation");
            q_requestForm.child(keyName).child("driver_number").setValue(TempPhone);
        }


    }







}
