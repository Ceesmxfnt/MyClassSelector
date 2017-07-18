package com.mixfinity.cees.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private static TextView button_log;
    private static Button button_sbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        OnButtonClickListener();
    }

    public void OnButtonClickListener() {
        button_log = (TextView)findViewById(R.id.reg_button_log);
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

}
