package com.group4.memoryv10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class PreviousImageGame extends AppCompatActivity {
    ImageView mainimg, img1, img2, img3, img4;
    TextView examinetxt, choosetxt, score, time;
    ArrayList<Integer> imagesList;
    ArrayList<ImageView> holdersList;
    int truecount, falsecount;
    Chronometer chronometer;
    String elapsedtime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_image_game);

        mainimg = findViewById(R.id.img);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);

        examinetxt = findViewById(R.id.txt1);
        choosetxt = findViewById(R.id.txt2);
        score = findViewById(R.id.Score);
        chronometer = findViewById(R.id.Time);
        chronometer.start();
        time = findViewById(R.id.Time);


        imagesList = new ArrayList<>();
        imagesList.add(R.drawable.ucgen);
        imagesList.add(R.drawable.kare);
        imagesList.add(R.drawable.besgen);
        imagesList.add(R.drawable.altigen);
        imagesList.add(R.drawable.eskenar);
        imagesList.add(R.drawable.yamuk);
        imagesList.add(R.drawable.yildiz1);
        imagesList.add(R.drawable.yildiz2);

        Collections.shuffle(imagesList);

        holdersList = new ArrayList<>();
        holdersList.add(img1);
        holdersList.add(img2);
        holdersList.add(img3);
        holdersList.add(img4);

        Collections.shuffle(holdersList);
        final int lightgreen = Color.parseColor("#E1FFF2");
        final int lightred = Color.parseColor("#FFE1E1");

        ImageView.OnClickListener imgClickListener = new ImageView.OnClickListener(){

            @Override
            public void onClick(final View v) {
                Handler handler = new Handler();

                if(mainimg.getTag().toString().equals(v.getTag().toString())){
                    truecount++;
                    score.setText("Skor: " + truecount);
                    v.setBackgroundColor(lightgreen);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetBackgroundColor(v);
                        }
                    },2000);

                }
                else{
                    falsecount++;
                    v.setBackgroundColor(lightred);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetBackgroundColor(v);
                        }
                    },2000);
                }

                if(imagesList.size()-1>3){
                    new CountDownTimer(2000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            imagesList.remove(0);
                            displayImages();
                        }
                    }.start();

                }

                else{
                    chronometer.stop();
                    elapsedtime = time.getText().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(PreviousImageGame.this);

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
                            Intent gamesint = new Intent(PreviousImageGame.this, GamesActivity.class);
                            startActivity(gamesint);
                        }
                    }.start();
                }
            }
        };

        img1.setOnClickListener(imgClickListener);
        img2.setOnClickListener(imgClickListener);
        img3.setOnClickListener(imgClickListener);
        img4.setOnClickListener(imgClickListener);

        displayImages();
    }

    public void displayImages(){
        mainimg.setImageResource(imagesList.get(0));
        mainimg.setTag(imagesList.get(0));
//        targetimg = PreviousImageGame.this.getResources().getResourceEntryName(imagesList.get(0));

        for (int i=0; i<4; i++){
            holdersList.get(i).setImageResource(imagesList.get(i));
            holdersList.get(i).setTag(imagesList.get(i));
        }
        choosetxt.setVisibility(View.INVISIBLE);
        examinetxt.setVisibility(View.VISIBLE);

        img1.setVisibility(View.INVISIBLE);
        img2.setVisibility(View.INVISIBLE);
        img3.setVisibility(View.INVISIBLE);
        img4.setVisibility(View.INVISIBLE);
        mainimg.setVisibility(View.VISIBLE);

        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                examinetxt.setVisibility(View.INVISIBLE);
                choosetxt.setVisibility(View.VISIBLE);
                mainimg.setVisibility(View.INVISIBLE);
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.VISIBLE);
                img4.setVisibility(View.VISIBLE);

            }
        }.start();



    }

    public void resetBackgroundColor(View v){
        v.setBackgroundColor(Color.TRANSPARENT);
    }

}
