package com.mixfinity.cees.login;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class LoginActivity extends AppCompatActivity {

    private static TextView button_reg;
    private static Button button_sbm;
    private static EditText input_email;
    private static EditText input_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        OnButtonClickListener();
    }

    public void OnButtonClickListener() {
        button_reg = (TextView)findViewById(R.id.log_reg_button);
        button_reg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(startIntent);
                    }
                }
        );
        button_sbm = (Button)findViewById(R.id.log_button_sbm);
        button_sbm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText input_email = (EditText) findViewById(R.id.input_login_email);
                        String Username = input_email.getText().toString();
                        input_email.setBackgroundResource(R.drawable.input);
                        if (Username.trim().equals("")) {
                            input_email.setBackgroundResource(R.drawable.input_error);
                            Toast.makeText(getBaseContext(), "Fill in a valid Username!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        EditText input_pass = (EditText) findViewById(R.id.input_login_pass);
                        String password = input_pass.getText().toString();
                        input_pass.setBackgroundResource(R.drawable.input);
                        if (password.trim().equals("")) {
                            input_pass.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.input_error));
                            Toast.makeText(getBaseContext(), "Fill in a valid Password!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
        );
    }
}