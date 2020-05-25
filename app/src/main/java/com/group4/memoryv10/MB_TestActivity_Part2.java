package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MB_TestActivity_Part2 extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference databaseReference, memoriesRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    StorageReference storageRef;
    private final String TAG = this.getClass().getName().toUpperCase();
    private List<Memory> memories;
    String[] choice;
    private List<Memory> memorieslist;
    long startTime, elapsedSeconds, totalreacttime;
    String reactionTime;
    ArrayList<Button> opts;
    ArrayList<String> ansflags, reactionTimes;
    Query mQuery;
    int memocount, remainingmemos, clickcount, truecount, falsecount, passcount;
    ImageView imgholder;
    TextView question;
    Button opt1, opt2, opt3, passbtn;
    String ans, correctans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mb_test_part2);

        opt1 = findViewById(R.id.btn1);
        opt2 = findViewById(R.id.btn2);
        opt3 = findViewById(R.id.btn3);
        passbtn = findViewById(R.id.passbtn);
        question = findViewById(R.id.questionholder);
        imgholder = findViewById(R.id.img1);

        //array list for memories
        memories = new ArrayList<>();

        //array list for text views
        opts = new ArrayList<>();
        opts.add(opt1);
        opts.add(opt2);
        opts.add(opt3);


        //array list to keep whether answers are true or false
        ansflags = new ArrayList<>();

        //array list for reaction times of each question
        reactionTimes = new ArrayList<>();

        //array for question types
        choice = new String[2];
        choice[0] = "date";
        choice[1] = "place";

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.d(user.getUid(), "userid");
        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();
        memoriesRef = databaseReference.child("Memories").child(user.getUid());
        storageRef= FirebaseStorage.getInstance().getReference();

        //order memories by date
        mQuery = memoriesRef.orderByChild("date");
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    Memory memory = datas.getValue(Memory.class);
                    //add all memories to array list
                    memories.add(memory);
                }

                //number of memories
                memocount = memories.size();
                Collections.shuffle(memories);
                memorieslist = memories;

                //count remaining memories
                remainingmemos = memocount;
                generateQuestion();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        //create custom button click listener for options
        Button.OnClickListener myOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //count questions
                clickcount++;

                //get end time to calculate reaction time
                long endTime = SystemClock.elapsedRealtime();

                //calculate reaction time and convert to seconds
                long elapsedTime = endTime - startTime;
                elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime);

                //calculate total reaction time
                totalreacttime += elapsedSeconds;

                //format reaction time to mm:ss
                reactionTime = DateUtils.formatElapsedTime(elapsedSeconds);

                //add each question's reaction time to array list
                reactionTimes.add(reactionTime);

                //get clicked button
                Button btn = findViewById(v.getId());
                ans = btn.getText().toString();

                //check answer and add flag to array list
                if (ans.equals(correctans)) {
                    ansflags.add("doğru");
                    truecount++;
                } else if (ans.equals("Hatırlamıyorum")) {
                    ansflags.add("pas");
                    passcount++;
                } else {
                    ansflags.add("yanlış");
                    falsecount++;
                }

                //check remaining memories
                checkremaining(remainingmemos, clickcount);
            }

        };

        //set custom onclick listener to options
        opt1.setOnClickListener(myOnClickListener);
        opt2.setOnClickListener(myOnClickListener);
        opt3.setOnClickListener(myOnClickListener);
        passbtn.setOnClickListener(myOnClickListener);

        //get start time to calculate reaction time
        startTime = SystemClock.elapsedRealtime();
    }

    //override onBackPressed function to alert user and cancel test
    @Override
    public void onBackPressed() {
        //create alert to cancel test
        AlertDialog.Builder alert = new AlertDialog.Builder(MB_TestActivity_Part2.this);
        alert.setTitle("Testi iptal etmek istiyor musunuz?");
        alert.setMessage("İlerlemeniz kaydedilmeyecek.");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                File file = new File(getExternalFilesDir(null), "mbresult.txt");
                if (file.exists()){
                    file.delete();
                }
                Intent cancelint = new Intent(MB_TestActivity_Part2.this, HomeActivity.class);
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

    //function to generate new question
    public void generateQuestion() {
        //shuffle list
        Collections.shuffle(memorieslist);
        Collections.shuffle(opts);

        //generate random to determine question
        Random generator = new Random();
        int randomIndex = generator.nextInt(choice.length);
        if (choice[randomIndex].equals("date")) {
            for(int i=0; i<3; i++){
                opts.get(i).setText(memorieslist.get(i).getDate());
            }

            //get correct answer
            correctans = memorieslist.get(0).getDate();
            question.setText("Yukarıdaki fotoğraf hangi tarihte çekildi?");
        } else {
            for(int i=0; i<3; i++){
                opts.get(i).setText(memorieslist.get(i).getPlace());
            }

            //get correct answer
            correctans = memorieslist.get(0).getPlace();
            question.setText("Yukarıdaki fotoğraf nerede çekildi?");
        }

        //retrieve image from firebase storage and insert into image holder
        storageRef.child("Users/" + memorieslist.get(0).getUserid() + "/Memories/" + memorieslist.get(0).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MB_TestActivity_Part2.this).load(uri.toString()).centerCrop().into(imgholder);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                Toast.makeText(MB_TestActivity_Part2.this, "Fotoğraf yüklenemedi.", Toast.LENGTH_LONG).show();
            }
        });

        //get start time to calculate reaction time
        startTime = SystemClock.elapsedRealtime();

        remainingmemos--;
    }

    public void toResults() {
        //calculate average reaction time
        long averagereacttime = totalreacttime / reactionTimes.size();
        String avg_reaction = DateUtils.formatElapsedTime(averagereacttime);

        //open the results text file of first part
        File file = new File(getExternalFilesDir(null), "mbresult.txt");
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            //add second part results to file
            buf.append("Bölüm 2: Çoktan Seçmeli Fotoğraf-Zaman/Mekan Eşleştirme");
            buf.newLine();
            buf.newLine();
            buf.append("Toplam Soru Sayısı:  ").append(String.valueOf(ansflags.size()));
            buf.newLine();
            buf.append("Doğru Cevap Sayısı:  ").append(String.valueOf(truecount));
            buf.newLine();
            buf.append("Yanlış Cevap Sayısı:  ").append(String.valueOf(falsecount));
            buf.newLine();
            buf.append("Boş Cevap Sayısı:  ").append(String.valueOf(passcount));
            buf.newLine();
            buf.append("Toplam Reaksiyon Süresi:  ").append(String.valueOf(reactionTime));
            buf.newLine();
            buf.append("Ortalama Reaksiyon Süresi:  ").append(String.valueOf(avg_reaction));
            buf.newLine();
            buf.append("Detaylı Sonuçlar:  ");
            buf.newLine();
            for (int i = 0; i < ansflags.size(); i++) {
                buf.append("Soru ").append(String.valueOf((i + 1))).append(":  ");
                buf.append("Cevap: ").append(String.valueOf(ansflags.get(i))).append("  ");
                buf.append("Reaksiyon Süresi: ").append(String.valueOf(reactionTimes.get(i))).append("  ");
                buf.newLine();
            }
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        user = mAuth.getCurrentUser();

        //get current time
        final String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        //use timestamp as the name of file
        final StorageReference fileRef = storageRef.child("Users/" + user.getUid() + "/MemoryBox Results/" + timeStamp + ".txt");

        //get file uri from file
        Uri fileuri = Uri.fromFile(file);

        //save results text file to firebase storage
        fileRef.putFile(fileuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fileurl = uri.toString();

                            }
                        });
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
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

        Toast.makeText(MB_TestActivity_Part2.this, "Sonuç kaydedildi.", Toast.LENGTH_LONG).show();

        //delete file from external storage
        file.delete();

        //redirect to home activity
        Intent intToHome = new Intent(MB_TestActivity_Part2.this, HomeActivity.class);
        startActivity(intToHome);
    }

    //function to check remaining memories
    public void checkremaining(int rem, int ct) {

        if (remainingmemos < 3 || clickcount >= 8) {
            toResults();
        } else{
            //remove previous memory from list
            memorieslist.remove(0);

            //generate new question
            generateQuestion();
        }
    }

    //save results text file url to database
    public void saveFileUrlToDatabase(String fileurl) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("MemoryBoxResults").child(user.getUid()).child(fileurl).setValue(fileurl);
    }

}


