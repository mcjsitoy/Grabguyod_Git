package com.example.grabguyod;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



       public void login(View view){
        Intent i = new Intent(this, Main2Activity.class);
           EditText u = (EditText) findViewById(R.id.uname);
           String welcome = u.getText().toString();
           i.putExtra(EXTRA_MESSAGE, welcome);
           startActivity(i);


    }











}
