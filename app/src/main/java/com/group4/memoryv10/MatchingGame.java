package com.group4.memoryv10;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MatchingGame extends AppCompatActivity {
    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11, image12;
    int FirstImage, SecondImage, number=1, Score=0, FirstView, SecondView, truecount, falsecount;
    TextView score,time;
    Integer[] array;
    List<Integer> list;
    ArrayList<ImageView> images;
    Chronometer chronometer;
    String elapsedtime;
    MediaPlayer myMediaPlayer = null;

    //rotate images
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
            score.setText("Skor: " + Score);
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

            //save reaction time to file
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Toast.makeText(MatchingGame.this,"Depolama alanına erişilemiyor.",Toast.LENGTH_LONG).show();
            }


            File file = new File(getExternalFilesDir(null), "mgresult.txt");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                buf.append("Sesli eşleştirme:");
                buf.newLine();
                buf.newLine();
                buf.append("Doğru Deneme Sayısı:  ").append(String.valueOf(truecount));
                buf.newLine();
                buf.append("Yanlış Deneme Sayısı:  ").append(String.valueOf(falsecount));
                buf.newLine();
                buf.append("Toplam Reaksiyon Süresi:  ").append(String.valueOf(elapsedtime));
                buf.newLine();
                buf.newLine();
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                AlertDialog.Builder builder = new AlertDialog.Builder(MatchingGame.this);

                builder.setTitle("Harika!");

                builder.setMessage("Tüm resimleri eşleştirdiniz. Sıra sessiz eşleştirmede.");

                final AlertDialog diag = builder.create();

                diag.show();

                new CountDownTimer(5000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        diag.dismiss();
                        Intent nextint = new Intent(MatchingGame.this, MatchingGame_NoAudio.class);
                        startActivity(nextint);
                    }
                }.start();
            }
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

        chronometer = findViewById(R.id.Time);
        chronometer.start();

        image1= findViewById(R.id.image1);
        image2= findViewById(R.id.image2);
        image3= findViewById(R.id.image3);
        image4= findViewById(R.id.image4);
        image5= findViewById(R.id.image5);
        image6= findViewById(R.id.image6);
        image7= findViewById(R.id.image7);
        image8= findViewById(R.id.image8);
        image9= findViewById(R.id.image9);
        image10= findViewById(R.id.image10);
        image11= findViewById(R.id.image11);
        image12= findViewById(R.id.image12);
        score = findViewById(R.id.Score);
        time = findViewById(R.id.Time);
        array = new Integer[]{1,2,3,4,5,6,1,2,3,4,5,6};
        list = Arrays.asList(array);
        Collections.shuffle(list);
        list.toArray(array);

        images = new ArrayList<>();
        images.add(image1);
        images.add(image2);
        images.add(image3);
        images.add(image4);
        images.add(image5);
        images.add(image6);
        images.add(image7);
        images.add(image8);
        images.add(image9);
        images.add(image10);
        images.add(image11);
        images.add(image12);

        for(int i=0; i<12; i++){
            final int finalI = i;
            images.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    ChangeImage(images.get(finalI),finalI);
                }
            });
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
