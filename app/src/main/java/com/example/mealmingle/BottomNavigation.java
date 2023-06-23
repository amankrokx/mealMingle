package com.example.mealmingle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;


public class BottomNavigation extends ConstraintLayout {
    private Context context;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickIntents();
    }

    public BottomNavigation(Context context) {
        super(context);
        this.context = context;
    }

    public BottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    // set the click intents for the bottom navigation buttons to navigate to the correct activity
    public void setClickIntents () {
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton historyButton = findViewById(R.id.historyButton);
        ImageButton profileButton = findViewById(R.id.profileButton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this.context, MainActivity.class);
            Log.d("BottomNavigation", "setClickIntents: " + this.context.toString());
            this.context.startActivity(intent);
        });
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this.context, History.class);
            Log.d("BottomNavigation", "setClickIntents: " + this.context.toString());
            this.context.startActivity(intent);
        });
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this.context, ProfileActivityActivity.class);
            Log.d("BottomNavigation", "setClickIntents: " + this.context.toString());
            this.context.startActivity(intent);
        });
    }

}
