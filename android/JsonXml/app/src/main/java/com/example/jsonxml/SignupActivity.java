package com.example.jsonxml;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void signUp (View v) {
        String name = ((EditText)findViewById(R.id.name)).getText().toString();
        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.editTextTextPassword)).getText().toString();

        // Password regex
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Check if the password matches the regex
        if (!password.matches(regex)) {
            Toast.makeText(this, "Password must contain atleast 1 uppercase, 1 lowercase, 1 digit, 1 special character and must be 8-20 characters long !", Toast.LENGTH_LONG).show();
            return;
        }

        // Username regex
        regex = "^[a-zA-Z0-9._-]{3,}$";

        // Check if the username matches the regex
        if (!username.matches(regex)) {
            Toast.makeText(this, "Username must contain atleast 3 characters and must contain only alphanumeric characters, underscore, hyphen and dot !", Toast.LENGTH_LONG).show();
            return;
        }

        // Name regex
        regex = "^[a-zA-Z\\s]*$";

        // Check if the name matches the regex
        if (!name.matches(regex)) {
            Toast.makeText(this, "Name must contain only alphabets and spaces !", Toast.LENGTH_LONG).show();
            return;
        }


        // Create an intent to send to login page, passing these details via bundle
        // display a toast message to show that the user has been created
        Toast.makeText(this, "SignUp Successfull !", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);

    }

    public void login (View v) {
        // Create an intent to send to login page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}