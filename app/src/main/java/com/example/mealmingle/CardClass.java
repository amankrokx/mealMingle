package com.example.mealmingle;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;


public class CardClass extends RelativeLayout {
//    description
//"Very tasty chola bhatura with chola leftover from morning an dbhatura strong as frisbee. You eat once and never live to eat another day. This way it makes you never hungry again."
//        (string)
//    expiresAt
//    May 29, 2023 at 9:39:45 PM UTC+5:30
//    hash
//"tdr1t"
//    hotelId
//"hotel1"
//    hotelName
//"Dominos"
//    lat
//12.9634
//    lng
//77.5855
//    name
//"Chola Bhatura"
//    nonVeg
//false
//    photoUrl
//"https://picsum.photos/seed/food/200/200"
//    price
//0
//    servings
//10
//}

    public String name;
    public String hotelName;
    public Double lat;
    public Double lng;
    public String hotelId;
    public String description;
    public String hash;
    public String photoUrl;
    public int servings;
    public boolean nonVeg;
    public TextView nameView;
    public ImageView imageView;
    public Button hotelNameView;
    public TextView servingsView;


    public CardClass(Context context) {
        super(context);
    }

    public CardClass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardClass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCard (String name, String hotelName, Double lat, Double lng, String hotelId, String description, String hash, String photoUrl, int servings, boolean nonVeg) {
        this.name = name;
        this.hotelName = hotelName;
        this.lat = lat;
        this.lng = lng;
        this.hotelId = hotelId;
        this.description = description;
        this.hash = hash;
        this.photoUrl = photoUrl;
        this.servings = servings;
        this.nonVeg = nonVeg;

        // set button to open google maps
        Button locationButton = findViewById(R.id.hotelName);
        locationButton.setOnClickListener(v -> onClickLocation());

        nameView = findViewById(R.id.foodName);
        imageView = findViewById(R.id.foodImage);
        hotelNameView = findViewById(R.id.hotelName);
        servingsView = findViewById(R.id.servings);

        nameView.setText(name);
        hotelNameView.setText(hotelName);
        servingsView.setText(servings + "");

        Log.d("PhotoUrl", photoUrl);

        Picasso.get().load(photoUrl).resize(112, 112).centerCrop().into(imageView);


    }
    public void onClickLocation () {
        //create intent to open google maps
        //pass in lat and long
        //start activity
          String uri = "http://maps.google.com/maps?daddr=" + this.lat + "," + this.lng;
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
          intent.setPackage("com.google.android.apps.maps");
          getContext().startActivity(intent);
    }
}
