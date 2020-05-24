package com.group4.memoryv10;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class RepeatedImagesGame extends AppCompatActivity {
    ImageView img;
    TextView score, time;
    ArrayList<Integer> imagesList, displayedImages;
    int truecount, falsecount, imagecount;
    Chronometer chronometer;
    String elapsedtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeated_images_game);

        img = findViewById(R.id.img);
        score = findViewById(R.id.Score);
        chronometer = findViewById(R.id.Time);
        chronometer.start();
        time = findViewById(R.id.Time);

        imagesList = new ArrayList<>();
        imagesList.add(R.drawable.fish1);
        imagesList.add(R.drawable.fish2);
        imagesList.add(R.drawable.fish3);
        imagesList.add(R.drawable.fish4);
        imagesList.add(R.drawable.fish5);
        imagesList.add(R.drawable.fish6);
        imagesList.add(R.drawable.fish7);
        imagesList.add(R.drawable.fish8);

        Collections.shuffle(imagesList);

        displayedImages = new ArrayList<>();

        imagecount = imagesList.size();

        img.setImageResource(imagesList.get(0));
        displayedImages.add(imagesList.get(0));

        Button.OnClickListener newClickListener = new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(displayedImages.contains(imagesList.get(0))){
                    falsecount++;
                }
                else{
                    truecount++;
                }
                checkRemainingImg();
            }
        };

        Button.OnClickListener repeatedClickListener = new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(displayedImages.contains(imagesList.get(0))){
                    truecount++;
                }
                else{
                    falsecount++;
                }
                checkRemainingImg();
            }
        };



    }

    public void displayImage(){
        imagesList.remove(0);
        imagecount--;
        img.setImageResource(imagesList.get(0));
        displayedImages.add(imagesList.get(0));
    }

    public void checkRemainingImg(){
        if(imagecount-1>1){
            displayImage();
        }

        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(RepeatedImagesGame.this);

            builder.setTitle("Tebrikler!");

            builder.setMessage("Oyunu tamamladınız.");

            final AlertDialog diag = builder.create();

            diag.show();

            new CountDownTimer(4000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    diag.dismiss();
                    Intent gamesint = new Intent(RepeatedImagesGame.this, GamesActivity.class);
                    startActivity(gamesint);
                }
            }.start();
        }
    }
}
