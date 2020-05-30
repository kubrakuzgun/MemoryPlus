package com.group4.memoryv10;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class RepeatedImagesGame extends AppCompatActivity {
    ImageView img, animatedimg;
    TextView score, time;
    Button newbtn, rptbtn;
    ArrayList<Integer> imagesList, displayedImages;
    int truecount, falsecount, imagecount;
    Chronometer chronometer;
    String elapsedtime;
    DatabaseReference databaseReference;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeated_images_game);

        img = findViewById(R.id.img);
        animatedimg = findViewById(R.id.animatedimg);
        score = findViewById(R.id.Score);
        chronometer = findViewById(R.id.Time);
        chronometer.start();
        time = findViewById(R.id.Time);
        newbtn = findViewById(R.id.newbtn);
        rptbtn = findViewById(R.id.repeatedbtn);

        imagesList = new ArrayList<>();
        imagesList.add(R.drawable.fish1);
        imagesList.add(R.drawable.fish2);
        imagesList.add(R.drawable.fish3);
        imagesList.add(R.drawable.fish4);
        imagesList.add(R.drawable.fish5);
        imagesList.add(R.drawable.fish6);
        imagesList.add(R.drawable.fish7);
        imagesList.add(R.drawable.fish8);
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
        animatedimg.setVisibility(View.INVISIBLE);

        Button.OnClickListener newClickListener = new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(displayedImages.contains(imagesList.get(0))){
                    falsecount++;
                    animatedimg.setImageResource(R.drawable.cross);
                    animateImg();
                }
                else{
                    truecount++;
                    score.setText("Skor: " + truecount);
                    animatedimg.setImageResource(R.drawable.check);
                    animateImg();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkRemainingImg();
                    }
                }, 2000);
            }

        };
        newbtn.setOnClickListener(newClickListener);

        Button.OnClickListener rptClickListener = new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(displayedImages.contains(imagesList.get(0))){
                    truecount++;
                    score.setText("Skor: " + truecount);
                    animatedimg.setImageResource(R.drawable.check);
                    animateImg();
                }
                else{
                    falsecount++;
                    animatedimg.setImageResource(R.drawable.cross);
                    animateImg();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkRemainingImg();
                    }
                }, 2000);
            }
        };
        rptbtn.setOnClickListener(rptClickListener);
    }

    public void displayImage(){
        displayedImages.add(imagesList.get(0));
        imagesList.remove(0);
        imagecount--;
        img.setImageResource(imagesList.get(0));
        newbtn.setEnabled(true);
        rptbtn.setEnabled(true);
    }

    public void checkRemainingImg(){
        if(imagecount-1>1){
            animatedimg.setVisibility(View.INVISIBLE);
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

    public void animateImg(){
        animatedimg.setVisibility(View.VISIBLE);
        animatedimg.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in));
        new CountDownTimer(700, 700) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                animatedimg.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out));
            }
        }.start();
    }

    public void saveResults() {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(RepeatedImagesGame.this, "Depolama alanına erişilemiyor.", Toast.LENGTH_LONG).show();
        }

        //create file to write results
        File file = new File(getExternalFilesDir(null), "riresult.txt");

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
            buf.append("Tekrarlayan resimleri bulma:");
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
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            storageRef = FirebaseStorage.getInstance().getReference();

            //get current time stamp to use as file name
            final String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            final StorageReference fileRef = storageRef.child("Users/" + user.getUid() + "/Game Results/RepeatedImages/" + timeStamp + ".txt");
            Uri fileuri = Uri.fromFile(file);

            //upload file to firebase storage
            fileRef.putFile(fileuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                }
                            });
                            //save file name(url) to firebase database
                            saveFileUrlToDatabase(timeStamp);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });

            //delete file from device
            file.delete();


            //create alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(RepeatedImagesGame.this);
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
                    Intent nextint = new Intent(RepeatedImagesGame.this, GamesActivity.class);
                    startActivity(nextint);
                }
            }.start();
        }
    }

    //override onBackPressed function to alert user and cancel test
    @Override
    public void onBackPressed() {
        //create alert to cancel test
        AlertDialog.Builder alert = new AlertDialog.Builder(RepeatedImagesGame.this);
        alert.setTitle("Testi iptal etmek istiyor musunuz?");
        alert.setMessage("İlerlemeniz kaydedilmeyecek.");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                File file = new File(getExternalFilesDir(null), "riresult.txt");
                if (file.exists()){
                    file.delete();
                }
                Intent cancelint = new Intent(RepeatedImagesGame.this, HomeActivity.class);
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

    //function to save file name(url) to firebase database
    public void saveFileUrlToDatabase(String fileurl) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("GameResults").child("RepeatedImages").child(user.getUid()).child(fileurl).setValue(fileurl);
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
