package com.example.mealmingle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class History extends AppCompatActivity {

    FirebaseUser user;
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );
    Button loginButton, logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        loginButton = findViewById(R.id.login);
        logoutButton = findViewById(R.id.logout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            WebView w = findViewById(R.id.historyWebView);
            WebSettings ws = w.getSettings();
            ws.setJavaScriptEnabled(true);
            w.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                    // Grant geolocation permission
                    callback.invoke(origin, true, false);
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ws.setGeolocationEnabled(true);
            }

            w.loadUrl("https://mealmingle.web.app/history#uid=" + user.getUid());
            ImageButton profilePic = findViewById(R.id.profileButton);
            Picasso.get().load(user.getPhotoUrl()).error(R.drawable.baseline_account_circle_24).placeholder(R.drawable.baseline_account_circle_24).into(profilePic);
            Log.d("MainActivity", user.toString());
        } else {
            // No user is signed in
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }
    public void login (View v) {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .setTheme(R.style.MealMingle)
                .build();
        signInLauncher.launch(signInIntent);
    }

    public void login () {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .setTheme(R.style.MealMingle)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // ...
            UserManager.initUser(user);
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Logged In Successfully !", Toast.LENGTH_SHORT).show();
            ImageButton profilePic = findViewById(R.id.profileButton);
            Picasso.get().load(user.getPhotoUrl()).error(R.drawable.baseline_account_circle_24).placeholder(R.drawable.baseline_account_circle_24).into(profilePic);
            Log.d("MainActivity", user.toString());
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "Error Login");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    public void logout (View v) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    // ...
//                        login();
                    Toast.makeText(this, "Logged Out Successfully !", Toast.LENGTH_SHORT).show();
                    loginButton.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.GONE);
                    ImageButton profilePic = findViewById(R.id.profileButton);
                    Picasso.get().load(R.drawable.baseline_account_circle_24).error(R.drawable.baseline_account_circle_24).placeholder(R.drawable.baseline_account_circle_24).into(profilePic);
                });
    }

}