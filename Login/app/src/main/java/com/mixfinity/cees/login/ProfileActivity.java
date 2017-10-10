package com.mixfinity.cees.login;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {

    private TextView profile_user_name;
    private TextView profile_user_email;
    private TextView profile_user_company;
    private TextView profile_user_address_street;
    private TextView profile_user_address_zip;
    private TextView profile_user_country;
    private TextView profile_user_phone;
    private ImageView profile_user_pic;

    private Button signOutButton;
    private Button backButton;

    private String mUserID;
    private String mName;
    private String mEmail;
    private Uri mPhotoUrl;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private DatabaseReference mUsersReference = mRootReference.child("users");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mUserID = user.getUid();
                    mName = user.getDisplayName();
                    mEmail = user.getEmail();
                    mPhotoUrl = user.getPhotoUrl();

                    setUserData();
                } else {
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));

                }
                 //...
            }
        };

        profile_user_name = (TextView) findViewById(R.id.profile_user_name);
        profile_user_email = (TextView) findViewById(R.id.profile_user_email);
        profile_user_company = (TextView) findViewById(R.id.profile_user_company);
        profile_user_address_street = (TextView) findViewById(R.id.profile_user_address_street);
        profile_user_address_zip = (TextView) findViewById(R.id.profile_user_address_zip);
        profile_user_country = (TextView) findViewById(R.id.profile_user_country);
        profile_user_phone = (TextView) findViewById(R.id.profile_user_phone);
        profile_user_pic = (ImageView) findViewById(R.id.profile_image);

        signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        backButton = (Button) findViewById(R.id.backToOption);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, OptionActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
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

    private void setUserData(){
        DatabaseReference mUserReference = mUsersReference.child(mUserID);

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fullName = (String) dataSnapshot.child("name").getValue();
                String company = (String) dataSnapshot.child("company").getValue();
                String address = (String) dataSnapshot.child("address").getValue();
                String[] addressArray = address.split(",");
                String country = (String) dataSnapshot.child("country").getValue();
                String[] countryArray = country.split(",");
                String phone = (String) dataSnapshot.child("phone").getValue().toString();
                String profile_pic = (String) dataSnapshot.child("profile_pic").getValue().toString();
                if(mName != null && mName == fullName){
                    fullName = mName;
                }
                if(mPhotoUrl != null) {
                    profile_pic = mPhotoUrl.toString();
                    loadImageFromUrl(profile_pic);
                }

                setUserDetails(fullName, mEmail, company, addressArray[0], addressArray[1], countryArray[0], phone);

                if(profile_pic != "false"){
                    loadImageFromUrl(profile_pic);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String error = databaseError.toString();
                profile_user_name.setText(error);
            }
        });
    }

    private void getGoogleData(){

    }

    private void getFacebookData(){

    }

    private void setUserDetails(String name, String email, String company, String addressStreet, String addressZip, String country, String phone){
        profile_user_name.setText(name);
        profile_user_email.setText(email);
        profile_user_company.setText(company);
        profile_user_address_street.setText(addressStreet);
        profile_user_address_zip.setText(addressZip);
        profile_user_country.setText(country);
        profile_user_phone.setText(phone);
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    public void loadImageFromUrl(String url) {
         Glide.with(this).load(url).into(profile_user_pic);
    }
}
