package com.example.grabguyod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class usertype_Select extends AppCompatActivity {
    Button bt_driver, bt_rider, bt_back;
    public static String selectedUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usertype__select);

        bt_driver = findViewById(R.id.button_driver);
        bt_rider = findViewById(R.id.button_rider);
        bt_back = findViewById(R.id.button_back);

        bt_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUserType = "drivers";
                Intent intent = new Intent(usertype_Select.this, user_login.class);
                startActivity(intent);
            }
        });

        bt_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUserType = "riders";
                Intent intent = new Intent(usertype_Select.this, user_login.class);
                startActivity(intent);
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(usertype_Select.this, Main3Activity.class);
                startActivity(intent);
            }
        });
    }
}
