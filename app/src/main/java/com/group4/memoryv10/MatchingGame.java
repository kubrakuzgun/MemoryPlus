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
import java.util.Collections;
import java.util.HashMap;

public class MatchingGame extends AppCompatActivity {
    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11, image12;
    int Score=0, truecount, falsecount, clickcount;
    int drw1,drw2, drw3, drw4, drw5, drw6;
    int audio1, audio2, audio3, audio4, audio5, audio6;
    AudioManager audioManager;
    HashMap<Integer, Integer> audioMap;
    TextView score,time;
    ArrayList<Integer> drawables, audios;
    ArrayList<ImageView> images, selectedviews;
    Chronometer chronometer;
    String elapsedtime;
    MediaPlayer myMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_game);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

        chronometer = findViewById(R.id.Time);
        chronometer.start();
        score = findViewById(R.id.Score);
        time = findViewById(R.id.Time);
        selectedviews = new ArrayList<>();

        audio1 = R.raw.gozluk;
        audio2 = R.raw.araba;
        audio3 = R.raw.ordek;
        audio4 = R.raw.kalem;
        audio5 = R.raw.telefon;
        audio6 = R.raw.gemi;

        //add audio files to an array list
        audios = new ArrayList<>();
        audios.add(audio1);
        audios.add(audio2);
        audios.add(audio3);
        audios.add(audio4);
        audios.add(audio5);
        audios.add(audio6);
        audios.add(audio1);
        audios.add(audio2);
        audios.add(audio3);
        audios.add(audio4);
        audios.add(audio5);
        audios.add(audio6);

        drw1 = R.drawable.gozluk;
        drw2 = R.drawable.araba;
        drw3 = R.drawable.ordek;
        drw4 = R.drawable.kalem;
        drw5 = R.drawable.telefon;
        drw6 = R.drawable.gemi;

        //add drawable images to an array list
        drawables = new ArrayList<>();
        drawables.add(drw1);
        drawables.add(drw2);
        drawables.add(drw3);
        drawables.add(drw4);
        drawables.add(drw5);
        drawables.add(drw6);
        drawables.add(drw1);
        drawables.add(drw2);
        drawables.add(drw3);
        drawables.add(drw4);
        drawables.add(drw5);
        drawables.add(drw6);

        //create a HashMap to match audios according to drawables
        audioMap = new HashMap<Integer, Integer>();
        for(int i=0; i<12; i++){
            audioMap.put(drawables.get(i), audios.get(i));
        }

        //shuffle drawables
        Collections.shuffle(drawables);

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

        //add image views to an array list
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
            //set onclick listener for each image view
            images.get(i).setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    clickcount++;
                    //prevent double click
                    v.setEnabled(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            changeImage(finalI);
                        }
                    },500);
                }
            });

            //insert shuffled drawables into image views and add tags
            images.get(i).setImageResource(R.drawable.lookup);
            images.get(i).setTag(drawables.get(i));
        }
    }

    //rotate image animation
    protected void animate(ImageView view, int res){
        final int resId = res;
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        final ImageView View = view;
        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                View.setImageResource(resId);
                oa2.start();
            }
        });
        oa1.start();
    }

    //play audio
    public void playMedia(int rawid){
        myMediaPlayer = MediaPlayer.create(MatchingGame.this, rawid);
        myMediaPlayer.start();
    }

    //change image view's drawable resource
    public void changeImage(int index){
        //add clicked views to an array list
        selectedviews.add(images.get(index));
        animate(images.get(index),drawables.get(index));
        //play audio according to drawable name
        playMedia(audioMap.get(drawables.get(index)));

        //when second image view is clicked
        if(clickcount == 2){
            //make other image views non-clickable
            for (int j=0; j<12;j++){
                images.get(j).setEnabled(false);
            }

            //create handler to run compare function with 2 seconds delay
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //call function to compare selected views
                    compare(selectedviews);
                    //reset click count
                    clickcount = 0;
                    //reset selected views array
                    selectedviews.clear();
                    for (int j=0; j<12;j++){
                        //make other image views clickable again
                        images.get(j).setEnabled(true);
                    }
                }
            },2000);
        }
    }

    //function to compare selected views by tag
    protected void compare(ArrayList<ImageView> selectedimages){
        //if the tags of selected views are the same
        if(selectedimages.get(0).getTag().equals(selectedimages.get(1).getTag()))
        {
            truecount++;
            Score+=1;
            score.setText("Skor: " + Score);
            //make views invisible
            selectedimages.get(0).setVisibility(View.INVISIBLE);
            selectedimages.get(1).setVisibility(View.INVISIBLE);
        }
        //if the tags of selected views are the different
        else
        {
            falsecount++;
            //revert views
            animate(selectedviews.get(0), R.drawable.lookup);
            animate(selectedviews.get(1), R.drawable.lookup);
        }

        //if all images are matched
        if (Score==6)
        {
            chronometer.stop();
            elapsedtime = time.getText().toString();

            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Toast.makeText(MatchingGame.this,"Depolama alanına erişilemiyor.",Toast.LENGTH_LONG).show();
            }

            //create file to write results
            File file = new File(getExternalFilesDir(null), "mgresult.txt");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                //add results to file
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
                //create alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MatchingGame.this);
                builder.setTitle("Harika!");
                builder.setMessage("Tüm resimleri eşleştirdiniz. Sıra sessiz eşleştirmede.");
                final AlertDialog diag = builder.create();
                diag.show();
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        //close dialog
                        diag.dismiss();
                        //redirect to second part
                        Intent nextint = new Intent(MatchingGame.this, MatchingGame_NoAudio.class);
                        startActivity(nextint);
                    }
                }.start();
            }
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
