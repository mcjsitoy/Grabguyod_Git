package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class list_street   extends ArrayAdapter<addRequest> {

    private Activity context;
    private List<addRequest> addRequestList;
    private String keyName, id_request,id_user,noP_user,offlineBroadcastStatus,status_request, timestamp,user_location, requestCode, uid;
    private Double lat, lng;
    public static Double translat ,translng;
    FirebaseUser user;
    DatabaseReference querythis, drivloc;

    public list_street(Activity context, List<addRequest> addRequestList){
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
        drivloc = FirebaseDatabase.getInstance().getReference("MarkerViewRequest");
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.activity_list_street,null,true);

        TextView textViewId = (TextView) listViewItem.findViewById(R.id.textView_Street);
        Button bt_select = listViewItem.findViewById(R.id.button_select);





        bt_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRequest add_req = new addRequest(id_request, id_user, offlineBroadcastStatus, "Waiting Confirmation",noP_user, timestamp,user_location, requestCode, uid);
                querythis.child(id_request).setValue(add_req);
            }
        });



        return listViewItem;
    }

}
