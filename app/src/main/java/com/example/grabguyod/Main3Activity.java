package com.example.grabguyod;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main3Activity extends AppCompatActivity {
    private Button mdriver, mrider, mgateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mdriver = (Button) findViewById(R.id.driver);
        mrider = (Button) findViewById(R.id.rider);
        mgateway = (Button) findViewById(R.id.gateway);

       mdriver.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(Main3Activity.this, usertype_Select.class);
               startActivity(intent);
               finish();
               return;
           }
       });

       mrider.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(Main3Activity.this, riderlogin.class);
               startActivity(intent);
               finish();
               return;

           }
       });

        mgateway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main3Activity.this, SMSgateway.class);
                startActivity(intent);
                finish();
                return;

            }
        });
    }
}
