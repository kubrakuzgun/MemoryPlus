package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MatchingGame_NoAudio extends AppCompatActivity {
    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11, image12;
    int Score=0, truecount, falsecount, clickcount;
    int drw1,drw2, drw3, drw4, drw5, drw6;
    int audio1, audio2, audio3, audio4, audio5, audio6;
    HashMap<Integer, Integer> audioMap;
    TextView score,time;
    ArrayList<Integer> drawables, audios;
    ArrayList<ImageView> images, selectedviews;
    Chronometer chronometer;
    String elapsedtime;
    DatabaseReference databaseReference;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_game_no_audio);

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

    //change image view's drawable resource
    public void changeImage(int index){
        //add clicked views to an array list
        selectedviews.add(images.get(index));
        animate(images.get(index),drawables.get(index));

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
            selectedviews.get(0).setEnabled(true);
            selectedviews.get(1).setEnabled(true);
        }

        //if all images are matched
        if (Score==6)
        {
            chronometer.stop();
            elapsedtime = time.getText().toString();

            //open file to write new results
            File file = new File(getExternalFilesDir(null), "mgresult.txt");

            try {
                //add new results
                BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                buf.append("Sessiz eşleştirme:");
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
            }
            finally {
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                storageRef = FirebaseStorage.getInstance().getReference();

                //get current time stamp to use as file name
                final String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                final StorageReference fileRef = storageRef.child("Users/" + user.getUid() + "/Game Results/Matching/" + timeStamp + ".txt");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MatchingGame_NoAudio.this);
                builder.setTitle("Tebrikler!");
                builder.setMessage("Oyunu tamamladınız.");
                final AlertDialog diag = builder.create();
                diag.show();
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        //close dialog
                        diag.dismiss();
                        //redirect to games activity
                        Intent gamesint = new Intent(MatchingGame_NoAudio.this, GamesActivity.class);
                        startActivity(gamesint);
                    }
                }.start();
            }
        }
    }

    //function to save file name(url) to firebase database
    public void saveFileUrlToDatabase(String fileurl) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("GameResults").child("Matching").child(user.getUid()).child(fileurl).setValue(fileurl);
    }

    //override onBackPressed function to alert user and cancel test
    @Override
    public void onBackPressed() {
        //create alert to cancel test
        AlertDialog.Builder alert = new AlertDialog.Builder(MatchingGame_NoAudio.this);
        alert.setTitle("Testi iptal etmek istiyor musunuz?");
        alert.setMessage("İlerlemeniz kaydedilmeyecek.");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                File file = new File(getExternalFilesDir(null), "mgresult.txt");
                if (file.exists()){
                    file.delete();
                }
                Intent cancelint = new Intent(MatchingGame_NoAudio.this, GamesActivity.class);
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
}
