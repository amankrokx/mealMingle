package com.example.jsonxml;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Log savedInstance state
        // Check if username is present in the bundle

        try {
            String username = getIntent().getExtras().getString("username");
            Toast.makeText(this, "Ready to Login !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("LoginActivity", "savedInstanceState is null");
            Toast.makeText(this, "First Signup .", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        }


    }

    public void login (View v) {
        // Get username and password from the user and match it with the username and password received from bundle
        // If the username and password matches, display a toast message saying "Login Successfull !"
        // Change the activity to the home page
        EditText _username = (EditText)findViewById(R.id.username);
        EditText _password = (EditText)findViewById(R.id.editTextTextPassword);
        String username = getIntent().getExtras().getString("username");
        String password = getIntent().getExtras().getString("password");

        if (_username.getText().toString().equals(username) && _password.getText().toString().equals(password)) {
            Toast.makeText(this, "Login Successfull !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, JSONtoXMLActivity.class);
            intent.putExtra("name", getIntent().getExtras().getString("name"));
            startActivity(intent);
        }
        else {
            if (username == null)
                Toast.makeText(this, "User not found !", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Login Failed !", Toast.LENGTH_LONG).show();
        }
    }

    public void signup (View v) {
        // Create an intent to send to signup page
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}