package com.mixfinity.cees.login;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static TextView button_reg;
    private static Button button_sbm;
    private static Button button_out_sbm;
    private static EditText input_email;
    private static EditText input_pass;

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private TextView mStatusTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    private String mUserID;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private DatabaseReference mUsersReference = mRootReference.child("users");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        OnButtonClickListener();

        mStatusTextView = (TextView) findViewById(R.id.status);
        mEmailField = (EditText) findViewById(R.id.input_login_email);
        mPasswordField = (EditText) findViewById(R.id.input_login_pass);

        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    updateUI(user);

                    mUserID = user.getUid();

                    DatabaseReference mUserReference = mUsersReference;

                    mUserReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.hasChild(mUserID)){
                                startActivity(new Intent(LoginActivity.this, RegisterExternActivity.class));
                            } else {
                                if (user.getProviders().get(0).equals("password")) {
                                    if(user.isEmailVerified() == true) {
                                        startActivity(new Intent(LoginActivity.this, OptionActivity.class));
                                    } else {
                                        startActivity(new Intent(LoginActivity.this, EmailVerificationActivity.class));
                                    }
                                } else {
                                    startActivity(new Intent(LoginActivity.this, OptionActivity.class));
                                }
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();
                    
                    findViewById(R.id.log_out_button_sbm).setVisibility(View.VISIBLE);
                    findViewById(R.id.log_button_sbm).setVisibility(View.GONE);

                } else {
                    // User is signed out
                    findViewById(R.id.log_out_button_sbm).setVisibility(View.GONE);
                    findViewById(R.id.log_button_sbm).setVisibility(View.VISIBLE);

                }
                // ...
            }
        };

        SignInButton mGoogleBtn = (SignInButton) findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // Error
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mCallbackManager = CallbackManager.Factory.create();

        LoginButton FacebookloginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        FacebookloginButton.setReadPermissions("email", "public_profile");
        
        FacebookloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Succes
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // Cancel
            }

            @Override
            public void onError(FacebookException error) {

                // Error
            }
        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    signOut();
                }
            }
        };
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{

                            updateUI(null);
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn(String email, String password, String type) {
        if(type.equals("normal")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.

                                updateUI(null);
                            }
                            if (!task.isSuccessful()) {
                                mEmailField.setBackgroundResource(R.drawable.input_error);
                                mPasswordField.setBackgroundResource(R.drawable.input_error);
                                mStatusTextView.setText(R.string.auth_failed);

                            }
                        }
                    });

        }
        if(type.equals("google")) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Error
            }
        }
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        updateUI(null);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }



                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));

        } else {
            mStatusTextView.setText(R.string.signed_out);

        }
    }

    public void OnButtonClickListener() {
        button_reg = (TextView)findViewById(R.id.log_reg_button);
        button_reg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    }
                }
        );
        button_sbm = (Button)findViewById(R.id.log_button_sbm);
        button_sbm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type = new String("normal");
                        EditText input_email = (EditText) findViewById(R.id.input_login_email);
                        String Email = input_email.getText().toString();
                        input_email.setBackgroundResource(R.drawable.input);
                        if (Email.trim().equals("")) {
                            input_email.setBackgroundResource(R.drawable.input_error);
                            Toast.makeText(getBaseContext(), "Fill in a valid Email!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        EditText input_pass = (EditText) findViewById(R.id.input_login_pass);
                        String Password = input_pass.getText().toString();
                        input_pass.setBackgroundResource(R.drawable.input);
                        if (Password.trim().equals("")) {
                            input_pass.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.input_error));
                            Toast.makeText(getBaseContext(), "Fill in a valid Password!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        signIn(Email, Password, type);
                    }
                }
        );
        button_out_sbm = (Button)findViewById(R.id.log_out_button_sbm);
        button_out_sbm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOut();
                    }
                }
        );
        findViewById(R.id.sign_in_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type = new String("google");
                        signIn(null,null,type);
                    }
                }
        );
    }

}