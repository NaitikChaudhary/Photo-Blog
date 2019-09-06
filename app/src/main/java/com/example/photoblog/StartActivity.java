package com.example.photoblog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mLogin, mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mLogin = (Button) findViewById(R.id.loginBtn);
        mRegister = (Button) findViewById(R.id.registerBtn);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToLoginActivity();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToRegisterActivity();
            }
        });

    }

    private void sendToRegisterActivity() {
        Intent loginIntent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(loginIntent);
    }

    private void sendToLoginActivity() {
        Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
