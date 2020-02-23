package com.example.grabguyod;

import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class list_currPass extends  ArrayAdapter<addRequest> {

    private Activity context;
    private List<addRequest> addRequestList;
    private String keyName, id_request,id_user,noP_user,offlineBroadcastStatus,status_request, timestamp,user_location, requestCode, uid;
    FirebaseUser user;
    DatabaseReference querythis;

    public list_currPass(Activity context, List<addRequest> addRequestList){
        super(context, R.layout.activity_list_layout, addRequestList);
        this.context = context;
        this.addRequestList = addRequestList;
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        querythis = FirebaseDatabase.getInstance().getReference("requestForm");
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.activity_list_layout,null,true);

        TextView textViewId = (TextView) listViewItem.findViewById(R.id.textView2);
        TextView textViewPhone = (TextView) listViewItem.findViewById(R.id.textView_location);
        TextView tv_broadcast = (TextView) listViewItem.findViewById(R.id.textView_noRiders);
        TextView tv_status = (TextView) listViewItem.findViewById(R.id.textView4);
        TextView tv_timestamp = (TextView) listViewItem.findViewById(R.id.textView_Timestamp);
        TextView tv_Location = listViewItem.findViewById(R.id.textView_location);
        TextView tv_NoRiders = listViewItem.findViewById(R.id.textView_noRiders);
        Button bt_add = listViewItem.findViewById(R.id.button_picked);


        final addRequest addRequest = addRequestList.get(position);
        id_request = addRequest.getRequest_id();
        id_user = addRequest.getUser_id();
        noP_user = addRequest.getUser_noP();
        offlineBroadcastStatus = addRequest.getOffline_BroadcastStatus();
        user_location = addRequest.getLocation();
        timestamp = addRequest.getTimeStamp();
        status_request = addRequest.getRequest_Status();
        requestCode = addRequest.getRequestCode();


/*
        textViewId.setText(addRequest.getUser_id());*/
        tv_timestamp.setText(addRequest.getTimeStamp());
        tv_Location.setText(addRequest.getLocation());
        tv_NoRiders.setText(addRequest.getUser_noP());




        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRequest add_req = new addRequest(id_request, id_user, offlineBroadcastStatus, "Waiting Confirmation",noP_user, timestamp,user_location, requestCode, uid);
                querythis.child(id_request).setValue(add_req);
            }
        });


        return listViewItem;
    }

}
