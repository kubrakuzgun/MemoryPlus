package com.group4.memoryv10;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MatchingGame extends AppCompatActivity {

    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;
    ImageView image6;
    ImageView image7;
    ImageView image8;
    ImageView image9;
    ImageView image10;
    ImageView image11;
    ImageView image12;
    ImageView restart ;
    TextView score ;
    TextView time ;
    Integer[] array ;
    List<Integer> list;
    Chronometer chronometer;
    int FirstImage;
    int SecondImage;
    int number =1;
    int Score =0;
    int FirstView;
    int SecondView;
    int truecount, falsecount;
    String elapsedtime;
    MediaPlayer myMediaPlayer = null;

    protected void Animate(ImageView view){
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        final ImageView View = view;
        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                View.setImageResource(R.drawable.lookup);
                oa2.start();
            }
        });
        oa1.start();
    }

    protected void compare(){
        if(FirstImage==SecondImage)
        {
            truecount++;
            Score+=1;
            score.setText("Score: " + Score);
            if(FirstView==0||SecondView==0)
                image1.setVisibility(View.INVISIBLE);
            if(FirstView==1||SecondView==1)
                image2.setVisibility(View.INVISIBLE);
            if(FirstView==2||SecondView==2)
                image3.setVisibility(View.INVISIBLE);
            if(FirstView==3||SecondView==3)
                image4.setVisibility(View.INVISIBLE);
            if(FirstView==4||SecondView==4)
                image5.setVisibility(View.INVISIBLE);
            if(FirstView==5||SecondView==5)
                image6.setVisibility(View.INVISIBLE);
            if(FirstView==6||SecondView==6)
                image7.setVisibility(View.INVISIBLE);
            if(FirstView==7||SecondView==7)
                image8.setVisibility(View.INVISIBLE);
            if(FirstView==8||SecondView==8)
                image9.setVisibility(View.INVISIBLE);
            if(FirstView==9||SecondView==9)
                image10.setVisibility(View.INVISIBLE);
            if(FirstView==10||SecondView==10)
                image11.setVisibility(View.INVISIBLE);
            if(FirstView==11||SecondView==11)
                image12.setVisibility(View.INVISIBLE);
        }
        else
        {
            falsecount++;
            if(FirstView==0||SecondView==0)
                Animate(image1);
            if(FirstView==1||SecondView==1)
                Animate(image2);
            if(FirstView==2||SecondView==2)
                Animate(image3);
            if(FirstView==3||SecondView==3)
                Animate(image4);
            if(FirstView==4||SecondView==4)
                Animate(image5);
            if(FirstView==5||SecondView==5)
                Animate(image6);
            if(FirstView==6||SecondView==6)
                Animate(image7);
            if(FirstView==7||SecondView==7)
                Animate(image8);
            if(FirstView==8||SecondView==8)
                Animate(image9);
            if(FirstView==9||SecondView==9)
                Animate(image10);
            if(FirstView==10||SecondView==10)
                Animate(image11);
            if(FirstView==11||SecondView==11)
                Animate(image12);
        }
        image1.setEnabled(true);
        image2.setEnabled(true);
        image3.setEnabled(true);
        image4.setEnabled(true);
        image5.setEnabled(true);
        image6.setEnabled(true);
        image7.setEnabled(true);
        image8.setEnabled(true);
        image9.setEnabled(true);
        image10.setEnabled(true);
        image11.setEnabled(true);
        image12.setEnabled(true);

        if (Score==6)
        {
            chronometer.stop();
            elapsedtime = time.getText().toString();
            AlertDialog.Builder end = new AlertDialog.Builder(MatchingGame.this);
            end.setMessage("Tüm şekilleri eşleştirdiniz!");
            end.setCancelable(false);
            end.setPositiveButton("Yeni Oyun", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    restart.performClick();
                }
            });
            end.setNegativeButton("Çıkış", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            end.create();
            end.show();
        }
    }
    protected void ChangeImage(ImageView view,int tag){
        if(array[tag]==1)
        {
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
            final ImageView View = view;
            oa1.setInterpolator(new DecelerateInterpolator());
            oa2.setInterpolator(new AccelerateDecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    View.setImageResource(R.drawable.araba);
                    oa2.start();
                }
            });
            oa1.start();

            myMediaPlayer = MediaPlayer.create(MatchingGame.this, R.raw.araba);
            myMediaPlayer.start();

        }
        else if(array[tag]==2){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
            final ImageView View = view;
            oa1.setInterpolator(new DecelerateInterpolator());
            oa2.setInterpolator(new AccelerateDecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    View.setImageResource(R.drawable.gozluk);
                    oa2.start();
                }
            });
            oa1.start();
            myMediaPlayer = MediaPlayer.create(MatchingGame.this, R.raw.gozluk);
            myMediaPlayer.start();
        }
        else if(array[tag]==3){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
            final ImageView View = view;
            oa1.setInterpolator(new DecelerateInterpolator());
            oa2.setInterpolator(new AccelerateDecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    View.setImageResource(R.drawable.telefon);
                    oa2.start();
                }
            });
            oa1.start();
            myMediaPlayer = MediaPlayer.create(MatchingGame.this, R.raw.telefon);
            myMediaPlayer.start();
        }
        else if(array[tag]==4){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
            final ImageView View = view;
            oa1.setInterpolator(new DecelerateInterpolator());
            oa2.setInterpolator(new AccelerateDecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    View.setImageResource(R.drawable.ordek);
                    oa2.start();
                }
            });
            oa1.start();
            myMediaPlayer = MediaPlayer.create(MatchingGame.this, R.raw.ordek);
            myMediaPlayer.start();
        }
        else if(array[tag]==5){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
            final ImageView View = view;
            oa1.setInterpolator(new DecelerateInterpolator());
            oa2.setInterpolator(new AccelerateDecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    View.setImageResource(R.drawable.gemi);
                    oa2.start();
                }
            });
            oa1.start();
            myMediaPlayer = MediaPlayer.create(MatchingGame.this, R.raw.gemi);
            myMediaPlayer.start();
        }
        else if(array[tag]==6){
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
            final ImageView View = view;
            oa1.setInterpolator(new DecelerateInterpolator());
            oa2.setInterpolator(new AccelerateDecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    View.setImageResource(R.drawable.kalem);
                    oa2.start();
                }
            });
            oa1.start();
            myMediaPlayer = MediaPlayer.create(MatchingGame.this, R.raw.kalem);
            myMediaPlayer.start();
        }
        if(number==1)
        {
            FirstImage=array[tag];
            number=2;
            FirstView= tag;
            if (tag ==0)
                image1.setEnabled(false);
            if(tag ==1)
                image2.setEnabled(false);
            if(tag==2)
                image3.setEnabled(false);
            if(tag==3)
                image4.setEnabled(false);
            if(tag==4)
                image5.setEnabled(false);
            if(tag==5)
                image6.setEnabled(false);
            if(tag==6)
                image7.setEnabled(false);
            if(tag ==7)
                image8.setEnabled(false);
            if(tag==8)
                image9.setEnabled(false);
            if(tag==9)
                image10.setEnabled(false);
            if(tag==10)
                image11.setEnabled(false);
            if(tag==11)
                image12.setEnabled(false);

        }
        else
        {
            SecondView= tag;
            SecondImage=array[tag];
            number=1;
            image1.setEnabled(false);
            image2.setEnabled(false);
            image3.setEnabled(false);
            image4.setEnabled(false);
            image5.setEnabled(false);
            image6.setEnabled(false);
            image7.setEnabled(false);
            image8.setEnabled(false);
            image9.setEnabled(false);
            image10.setEnabled(false);
            image11.setEnabled(false);
            image12.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    compare();
                }
            },2000);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_game);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

        chronometer = (Chronometer)findViewById(R.id.Time);
        chronometer.start();

        image1= (ImageView) findViewById(R.id.image1);
        image2= (ImageView) findViewById(R.id.image2);
        image3= (ImageView) findViewById(R.id.image3);
        image4= (ImageView) findViewById(R.id.image4);
        image5= (ImageView) findViewById(R.id.image5);
        image6= (ImageView) findViewById(R.id.image6);
        image7= (ImageView) findViewById(R.id.image7);
        image8= (ImageView) findViewById(R.id.image8);
        image9= (ImageView) findViewById(R.id.image9);
        image10= (ImageView) findViewById(R.id.image10);
        image11= (ImageView) findViewById(R.id.image11);
        image12= (ImageView) findViewById(R.id.image12);
        restart = (ImageView) findViewById(R.id.restart);
        score = (TextView) findViewById(R.id.Score);
        time = (TextView) findViewById(R.id.Time);
        array = new Integer[]{1,2,3,4,5,6,1,2,3,4,5,6};
        list = Arrays.asList(array);
        Collections.shuffle(list);
        list.toArray(array);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image1,0);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image2,1);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image3,2);
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image4,3);
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image5,4);
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image6,5);
            }
        });
        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image7,6);
            }
        });
        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image8,7);
            }
        });
        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image9,8);
            }
        });
        image10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image10,9);
            }
        });
        image11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image11,10);
            }
        });
        image12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ChangeImage(image12,11);
            }
        });


    }

    public void onRestartClick(View v){
        Collections.shuffle(list);
        list.toArray(array);
        image1.setImageResource(R.drawable.lookup);
        image2.setImageResource(R.drawable.lookup);
        image3.setImageResource(R.drawable.lookup);
        image4.setImageResource(R.drawable.lookup);
        image5.setImageResource(R.drawable.lookup);
        image6.setImageResource(R.drawable.lookup);
        image7.setImageResource(R.drawable.lookup);
        image8.setImageResource(R.drawable.lookup);
        image9.setImageResource(R.drawable.lookup);
        image10.setImageResource(R.drawable.lookup);
        image11.setImageResource(R.drawable.lookup);
        image12.setImageResource(R.drawable.lookup);

        image1.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        image4.setVisibility(View.VISIBLE);
        image5.setVisibility(View.VISIBLE);
        image6.setVisibility(View.VISIBLE);
        image7.setVisibility(View.VISIBLE);
        image8.setVisibility(View.VISIBLE);
        image9.setVisibility(View.VISIBLE);
        image10.setVisibility(View.VISIBLE);
        image11.setVisibility(View.VISIBLE);
        image12.setVisibility(View.VISIBLE);
        Score=0;
        score.setText("Skor: 0");

        long systemCurrTime = SystemClock.elapsedRealtime();
        chronometer.setBase(systemCurrTime);
        chronometer.start();
    }
}
