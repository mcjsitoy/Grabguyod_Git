package com.example.grabguyod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class driver_editProfile extends AppCompatActivity {

    Button bt_back, bt_editInfo, bt_changePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_edit_profile);

        bt_back = findViewById(R.id.button_back);


        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(driver_editProfile.this, driver_landingpage.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

}
