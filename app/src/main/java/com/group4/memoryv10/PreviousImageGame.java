package com.group4.memoryv10;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
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

        //add drawables to array list
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

        //add image views to array list
        holdersList = new ArrayList<>();
        holdersList.add(img1);
        holdersList.add(img2);
        holdersList.add(img3);
        holdersList.add(img4);
        Collections.shuffle(holdersList);

        final int lightgreen = Color.parseColor("#E1FFF2");
        final int lightred = Color.parseColor("#FFE1E1");

        //create onclick listener for image holders
        ImageView.OnClickListener imgClickListener = new ImageView.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Handler handler = new Handler();
                //compare tags
                // if tags are equal
                if (mainimg.getTag().toString().equals(v.getTag().toString())) {
                    truecount++;
                    score.setText("Skor: " + truecount);
                    v.setBackgroundColor(lightgreen);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetBackgroundColor(v);
                        }
                    }, 2000);
                }
                //if tags are not equal
                else {
                    falsecount++;
                    v.setBackgroundColor(lightred);
                    //wait for 2 seconds
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetBackgroundColor(v);
                        }
                    }, 2000);
                }

                //check remaining images
                if (imagesList.size() - 1 > 3) {
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            //remove previously asked image
                            imagesList.remove(0);
                            //create new question
                            displayImages();
                        }
                    }.start();

                } else {
                    chronometer.stop();
                    elapsedtime = time.getText().toString();
                    saveResults();
                }
            }
        };

        img1.setOnClickListener(imgClickListener);
        img2.setOnClickListener(imgClickListener);
        img3.setOnClickListener(imgClickListener);
        img4.setOnClickListener(imgClickListener);

        displayImages();
    }

    public void displayImages() {
        mainimg.setImageResource(imagesList.get(0));
        mainimg.setTag(imagesList.get(0));

        Collections.shuffle(holdersList);

        for (int i = 0; i < 4; i++) {
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

        //change view after 5 seconds (switch to question)
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

    //make background color transparent
    public void resetBackgroundColor(View v) {
        v.setBackgroundColor(Color.TRANSPARENT);
    }

    public void saveResults() {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(PreviousImageGame.this, "Depolama alanına erişilemiyor.", Toast.LENGTH_LONG).show();
        }

        //create file to write results
        File file = new File(getExternalFilesDir(null), "piresult.txt");

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
            buf.append("Önceki resmi bulma 1: Kolay seviye:");
            buf.newLine();
            buf.newLine();
            buf.append("Doğru Sayısı:  ").append(String.valueOf(truecount));
            buf.newLine();
            buf.append("Yanlış Sayısı:  ").append(String.valueOf(falsecount));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(PreviousImageGame.this);
            builder.setTitle("Birince seviye tamamlandı!");
            builder.setMessage("Bir sonraki seviye için hazır olun.");
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
                    Intent nextint = new Intent(PreviousImageGame.this, PreviousImageGame_Level2.class);
                    startActivity(nextint);
                }
            }.start();
        }
    }

    //override onBackPressed function to alert user and cancel test
    @Override
    public void onBackPressed() {
        //create alert to cancel test
        AlertDialog.Builder alert = new AlertDialog.Builder(PreviousImageGame.this);
        alert.setTitle("Testi iptal etmek istiyor musunuz?");
        alert.setMessage("İlerlemeniz kaydedilmeyecek.");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                File file = new File(getExternalFilesDir(null), "piresult.txt");
                if (file.exists()){
                    file.delete();
                }
                Intent cancelint = new Intent(PreviousImageGame.this, GamesActivity.class);
                startActivity(cancelint);
            }
        });
        alert.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
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
