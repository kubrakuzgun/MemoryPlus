package com.group4.memoryv10;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GamesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Mini Oyunlar");
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    public void toMatchingGame(View v){
        Intent mgint = new Intent(GamesActivity.this, MatchingGame.class);
        startActivity(mgint);
    }

    public void toPreviousImageGame(View v){
        Intent piint = new Intent(GamesActivity.this, PreviousImageGame.class);
        startActivity(piint);
    }
    public void toRepeatedImagesGame(View v){
        Intent riint = new Intent(GamesActivity.this, RepeatedImagesGame.class);
        startActivity(riint);
    }

}
