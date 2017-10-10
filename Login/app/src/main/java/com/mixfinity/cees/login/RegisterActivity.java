package com.mixfinity.cees.login;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static TextView button_log;
    private static Button button_sbm;
    private static Button button_next_p1;
    private static Button button_back_p1;
    private static Button button_next_p2;
    private static Button button_back_p2;
    private static Button button_back_p3;
    private static EditText input_name;
    private static EditText input_email;
    private static EditText input_company;
    private static EditText input_add_street;
    private static EditText input_add_number;
    private static EditText input_add_zip;
    private static EditText input_add_city;
    private static EditText input_date;
    private static EditText input_country;
    private static EditText input_phone;
    private static EditText input_pass;
    private static EditText input_pass_confirm;

    private static String regEmail;
    private static String regCompany;
    private static String regCountry;
    private static String regName;
    private static String regAddStreet;
    private static String regAddNr;
    private static String regAddZip;
    private static String regAddCity;
    private static String regDate;
    private static String regPhone;
    private static String regPass;
    private static String regPassConf;

    private String mUserID;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private DatabaseReference mUsersReference = mRootReference.child("users");

    private static Boolean registration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        OnButtonClickListener();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if(registration == true) {

                    } else {
                        startActivity(new Intent(RegisterActivity.this, OptionActivity.class));
                    }

                } else {
                    // User is signed out

                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signUp(String email, String password) {
        registration = true;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Registration Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateFirebaseUser(regName, null);
                            user.sendEmailVerification();
                            mUserID = user.getUid().toString();
                            mUsersReference.child(mUserID).child("address").setValue(regAddStreet + " " + regAddNr + ", " + regAddZip + " " +regAddCity);
                            mUsersReference.child(mUserID).child("company").setValue(regCompany);
                            mUsersReference.child(mUserID).child("country").setValue(regCountry);
                            mUsersReference.child(mUserID).child("email").setValue(regEmail);
                            mUsersReference.child(mUserID).child("name").setValue(regName);
                            mUsersReference.child(mUserID).child("phone").setValue(regPhone);
                            mUsersReference.child(mUserID).child("profile_pic").setValue(false);

                            startActivity(new Intent(RegisterActivity.this, OptionActivity.class));
                        }

                    }
                });
    }

    public void updateFirebaseUser(String newDisplayName, Uri newPhotoUri) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        if (user == null || (TextUtils.isEmpty(newDisplayName) && newPhotoUri == null)) {
            return;
        }
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();

        if (!TextUtils.isEmpty(newDisplayName)) {
            builder.setDisplayName(newDisplayName);
        }
        if (newPhotoUri != null) {
            builder.setPhotoUri(newPhotoUri);
        }
        UserProfileChangeRequest request = builder.build();
        user.updateProfile(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getBaseContext(), "Update Succes!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void OnButtonClickListener() {

        input_email = (EditText) findViewById(R.id.reg_input_email);
        input_company = (EditText) findViewById(R.id.reg_input_comp);
        input_country = (EditText) findViewById(R.id.reg_input_country);
        input_name = (EditText) findViewById(R.id.reg_input_name);
        input_add_street = (EditText) findViewById(R.id.reg_input_add_street);
        input_add_number = (EditText) findViewById(R.id.reg_input_add_numb);
        input_add_zip = (EditText) findViewById(R.id.reg_input_add_zip);
        input_add_city = (EditText) findViewById(R.id.reg_input_add_city);
        input_date = (EditText) findViewById(R.id.reg_input_date);
        input_phone = (EditText) findViewById(R.id.reg_input_phone);
        input_pass = (EditText) findViewById(R.id.reg_input_pass);
        input_pass_confirm = (EditText) findViewById(R.id.reg_input_pass_conf);

        setInputText();

        if(findViewById(R.id.reg_button_log) != null) {
            button_log = (TextView) findViewById(R.id.reg_button_log);
            button_log.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent startIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(startIntent);
                        }
                    }
            );
        }
        if(findViewById(R.id.next_button_p1) != null) {
            button_next_p1 = (Button) findViewById(R.id.next_button_p1);
            button_next_p1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    regEmail = input_email.getText().toString();
                    regCompany = input_company.getText().toString();
                    regCountry = input_country.getText().toString();

                    setContentView(R.layout.activity_register_2);
                    OnButtonClickListener();

                }
            });
        }
        if(findViewById(R.id.back_button_p1) != null) {
            button_back_p1 = (Button) findViewById(R.id.back_button_p1);
            button_back_p1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(startIntent);
                }
            });
        }
        if(findViewById(R.id.next_button_p2) != null) {
            button_next_p2 = (Button) findViewById(R.id.next_button_p2);
            button_next_p2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    regName = input_name.getText().toString();
                    regAddStreet = input_add_street.getText().toString();
                    regAddNr = input_add_number.getText().toString();
                    regAddZip = input_add_zip.getText().toString();
                    regAddCity = input_add_city.getText().toString();
                    regDate = input_date.getText().toString();
                    regPhone = input_phone.getText().toString();

                    setContentView(R.layout.activity_register_3);
                    OnButtonClickListener();
                }
            });
        }
        if(findViewById(R.id.back_button_p2) != null) {
            button_back_p2 = (Button) findViewById(R.id.back_button_p2);
            button_back_p2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.activity_register);
                    OnButtonClickListener();
                }
            });
        }

        if(findViewById(R.id.back_button_p3) != null) {
            button_back_p3 = (Button) findViewById(R.id.back_button_p3);
            button_back_p3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.activity_register_2);
                    OnButtonClickListener();
                }
            });
        }
        if(findViewById(R.id.reg_button_sbm) != null){
            button_sbm = (Button) findViewById(R.id.reg_button_sbm);
            button_sbm.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            input_pass = (EditText) findViewById(R.id.reg_input_pass);
                            String Password = input_pass.getText().toString();

                            signUp(regEmail, Password);
                        }
                    }
            );
        }
        if(findViewById(R.id.reg_input_pass) != null) {
            input_pass = (EditText) findViewById(R.id.reg_input_pass);
            input_pass.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(s.length()==0)
                        input_pass.setBackgroundResource(R.drawable.input_easy);
                    else if(s.length()<6)
                        input_pass.setBackgroundResource(R.drawable.input_easy);
                    else if(s.length()<10)
                        input_pass.setBackgroundResource(R.drawable.input_medium);
                    else if(s.length()<15)
                        input_pass.setBackgroundResource(R.drawable.input_hard);
                    else
                        input_pass.setBackgroundResource(R.drawable.input_hard);

                    if(s.length()>=20)
                        input_pass.setBackgroundResource(R.drawable.input_easy);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }


    public void setInputText(){
        if(findViewById(R.id.reg_input_email) != null) {
            if (regEmail != null && !regEmail.isEmpty()) {
                input_email.setText(regEmail);
            }
        }
        if(findViewById(R.id.reg_input_comp) != null) {
            if (regCompany != null && !regCompany.isEmpty()) {
                input_company.setText(regCompany);
            }
        }
        if(findViewById(R.id.reg_input_country) != null) {
            if (regCountry != null && !regCountry.isEmpty()) {
                input_country.setText(regCountry);
            }
        }
        if(findViewById(R.id.reg_input_name) != null) {
            if (regName != null && !regName.isEmpty()) {
                input_name.setText(regName);
            }
        }
        if(findViewById(R.id.reg_input_add_street) != null) {
            if (regAddStreet != null && !regAddStreet.isEmpty()) {
                input_add_street.setText(regAddStreet);
            }
        }
        if(findViewById(R.id.reg_input_add_numb) != null) {
            if (regAddNr != null && !regAddNr.isEmpty()) {
                input_add_number.setText(regAddNr);
            }
        }
        if(findViewById(R.id.reg_input_add_zip) != null) {
            if (regAddZip != null && !regAddZip.isEmpty()) {
                input_add_zip.setText(regAddZip);
            }
        }
        if(findViewById(R.id.reg_input_add_city) != null) {
            if (regAddCity != null && !regAddCity.isEmpty()) {
                input_add_city.setText(regAddCity);
            }
        }
        if(findViewById(R.id.reg_input_date) != null) {
            if (regDate != null && !regDate.isEmpty()) {
                input_date.setText(regDate);
            }
        }
        if(findViewById(R.id.reg_input_phone) != null) {
            if (regPhone != null && !regPhone.isEmpty()) {
                input_phone.setText(regPhone);
            }
        }
    }
}