package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class riderlogin extends AppCompatActivity {
    private TextView memail, mpassword;
    private Button dregister, bt_back;
    private TextView tv_label_License, tv_label_PhoneNum, tv_License,tv_PhoneNumber, tv_fullname, tv_licensePlate;
    private  EditText et_licensePlate;
    private RadioGroup rg_select;
    private RadioButton rb_driver, rb_rider, rb_offlinedriver;
    private FirebaseAuth mauth;
    private FirebaseAuth.AuthStateListener firebaseauthlistener;
    private String c_UserType;
    DatabaseReference db_reqForm;
    FirebaseUser user;
    public String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riderlogin);

        mauth = FirebaseAuth.getInstance();

        firebaseauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    if (c_UserType == "riders") {
                        Intent intent = new Intent(riderlogin.this, requestForm.class);
                        startActivity(intent);
                        finish();
                    } else  {
                        Intent intent = new Intent(riderlogin.this, driver_landingpage.class);
                        startActivity(intent);
                        finish();
                    }

                }

            }
        };
        bt_back = (Button) findViewById(R.id.button_back);
        rg_select = (RadioGroup) findViewById(R.id.radioUserType);
        memail = (EditText) findViewById(R.id.email);
        mpassword = (EditText) findViewById(R.id.password);
        dregister = (Button) findViewById(R.id.drregister);
        tv_License = (TextView) findViewById(R.id.tv_LicenseNumber);
        tv_PhoneNumber = (TextView) findViewById(R.id.tv_PhoneNumber);
        tv_fullname = (TextView) findViewById(R.id.tv_FullName);
        tv_label_License = (TextView) findViewById(R.id.tv_label_DriverLicense);
        tv_label_PhoneNum= (TextView) findViewById(R.id.tv_Label_PhoneNumber);
        tv_licensePlate = (TextView) findViewById(R.id.textView_Driver);
        et_licensePlate = (EditText) findViewById(R.id.editText_licensePlate);
        rb_driver = findViewById(R.id.radioDriver);
        rb_rider = findViewById(R.id.radioRider);
        rb_offlinedriver = findViewById(R.id.radioButton_offlineDriver);




        rb_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_PhoneNumber.setVisibility(View.INVISIBLE);
                tv_License.setVisibility(View.INVISIBLE);
                tv_label_License.setVisibility(View.INVISIBLE);
                tv_label_PhoneNum.setVisibility(View.INVISIBLE);
                tv_licensePlate.setVisibility(View.INVISIBLE);
                et_licensePlate.setVisibility(View.INVISIBLE);
                c_UserType = "riders";
            }
        });

        rb_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_PhoneNumber.setVisibility(View.VISIBLE);
                tv_License.setVisibility(View.VISIBLE);
                tv_label_License.setVisibility(View.VISIBLE);
                tv_label_PhoneNum.setVisibility(View.VISIBLE);
                tv_licensePlate.setVisibility(View.VISIBLE);
                et_licensePlate.setVisibility(View.VISIBLE);
                c_UserType = "drivers";
            }
        });

        rb_offlinedriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_PhoneNumber.setVisibility(View.VISIBLE);
                tv_License.setVisibility(View.VISIBLE);
                tv_label_License.setVisibility(View.VISIBLE);
                tv_label_PhoneNum.setVisibility(View.VISIBLE);
                tv_licensePlate.setVisibility(View.VISIBLE);
                et_licensePlate.setVisibility(View.VISIBLE);
                c_UserType = "offlineDrivers";
            }
        });


        //Register Function
        dregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = memail.getText().toString();
                final String password = mpassword.getText().toString();
                final String fullanme = tv_fullname.getText().toString();
                final String license = tv_License.getText().toString();
                final String phonenumber = tv_PhoneNumber.getText().toString();
                final String licensePlate = et_licensePlate.getText().toString();
                int checkedId = rg_select.getCheckedRadioButtonId();
                if (checkedId == -1) {
                    //No Radio button selected
                    Toast.makeText(riderlogin.this, "Select User Type", Toast.LENGTH_SHORT).show();
                } else {
                    mauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(riderlogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(riderlogin.this, "Sign-up error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(riderlogin.this, "Register Success", Toast.LENGTH_SHORT).show();
                                db_reqForm = FirebaseDatabase.getInstance().getReference("users");
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                uid = user.getUid();

                                if (c_UserType.equals("riders")) {
                                    addRegister add_req = new addRegister(email, password, fullanme, c_UserType);
                                    db_reqForm.child(c_UserType).child(uid).setValue(add_req);
                                    Toast.makeText(riderlogin.this, "Register Success", Toast.LENGTH_SHORT).show();
                                } else if (c_UserType.equals("drivers")){
                                    addRegister add_req = new addRegister(email, password, fullanme, c_UserType, license, phonenumber, licensePlate);
                                    db_reqForm.child(c_UserType).child(uid).setValue(add_req);
                                    Toast.makeText(riderlogin.this, "Register Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    addRegister add_req = new addRegister(email, password, fullanme, c_UserType, license, phonenumber, licensePlate);
                                    db_reqForm.child(c_UserType).child(uid).setValue(add_req);
                                    Toast.makeText(riderlogin.this, "Register Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(riderlogin.this, Main3Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        }
                    });
                }
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(riderlogin.this, Main3Activity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(firebaseauthlistener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mauth.removeAuthStateListener(firebaseauthlistener);

    }
}
