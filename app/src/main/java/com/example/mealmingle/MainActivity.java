package com.example.mealmingle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    Button loginButton, logoutButton;
    GeoLocation center;
    final double radius = 20000; // 20 km
    FirebaseUser user;
    LinearLayout l;
    Context c;
    LayoutInflater inflater;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "Started");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login);
        logoutButton = findViewById(R.id.logout);

        l = findViewById(R.id.cards);
        c = l.getContext();
        inflater = LayoutInflater.from(c);

        // Check if user is signed in (non-null) and update UI accordingly.
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("MainActivity", "User is signed in");
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            UserManager.initUser(user);
            ImageButton profilePic = findViewById(R.id.profileButton);
            Picasso.get().load(user.getPhotoUrl()).error(R.drawable.baseline_account_circle_24).placeholder(R.drawable.baseline_account_circle_24).into(profilePic);
            Log.d("MainActivity", user.toString());
        } else {
            Log.d("MainActivity", "User is not signed in");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            String url = "http://ip-api.com/json"; // Replace with your API endpoint

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            String lat = response.getString("lat");
                            String lon = response.getString("lon");
                            center = new GeoLocation(Double.parseDouble(lat), Double.parseDouble(lon));
                            Log.d("Coordinates", lat + ", " + lon);
                            listenToDatabase();

                            // Process the user object
                        } catch (Exception e) {
                            Log.e("Coordinates", "Error");
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        // Handle error
                        Log.e("Coordinates", "Error");
                        error.printStackTrace();
                    });

            requestQueue.add(request);







    }

    public void listenToDatabase () {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("meals")
                    .orderBy("hash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(t -> {
                    List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                    for (Task<QuerySnapshot> task : tasks) {
                        QuerySnapshot snap = task.getResult();
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            double lat = doc.getDouble("lat");
                            double lng = doc.getDouble("lng");

                            // We have to filter out a few false positives due to GeoHash
                            // accuracy, but most will match
                            GeoLocation docLocation = new GeoLocation(lat, lng);
                            double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                            if (distanceInM <= radius && doc.get("servings", Integer.class) > 0) {
                                matchingDocs.add(doc);
                            }
                        }
                    }
                    // matchingDocs contains the results
                    // ...
                    Log.e("Matching Docs", matchingDocs.toString());
                    for (DocumentSnapshot doc : matchingDocs) {
                        Log.e("Matching Docs", doc.toString());
                        CardClass customViewGroup = (CardClass) inflater.inflate(R.layout.card, null);
                        l.addView(customViewGroup);
                        // public void setCard (String name, String hotelName, Double lat, Double lng, String hotelId, String description, String hash, String photoUrl, int servings, boolean nonVeg)
                        customViewGroup.setCard(
                                doc.get("name", String.class),
                                doc.get("hotelName", String.class),
                                doc.get("lat", Double.class),
                                doc.get("lng", Double.class),
                                doc.get("hotelId", String.class),
                                doc.get("description", String.class),
                                doc.get("hash", String.class),
                                doc.get("photoUrl", String.class),
                                doc.get("servings", Integer.class),
                                doc.get("nonVeg", Boolean.class)
                        );
                    }
                    // add bottom_navigation_span at the end to keep the last card visible

                });
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

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // ...
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
        MainActivity act = this;
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    // ...
//                        login();
                    Toast.makeText(act, "Logged Out Successfully !", Toast.LENGTH_SHORT).show();
                    loginButton.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.GONE);
                    ImageButton profilePic = findViewById(R.id.profileButton);
                    Picasso.get().load(R.drawable.baseline_account_circle_24).error(R.drawable.baseline_account_circle_24).placeholder(R.drawable.baseline_account_circle_24).into(profilePic);

                });
    }

}