package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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


    private List<Memory> allmemories;
    private List<Memory> randommemories;
    private List<Memory> memorieslist;

    long startTime, elapsedSeconds, totalreacttime;
    String reactionTime;

    ArrayList<ImageView> imgs;
    ArrayList<TextView> opts;
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
        randommemories = new ArrayList<>();
        memories = new ArrayList<>();
        imgs = new ArrayList<>();
        opts = new ArrayList<>();

        question = findViewById(R.id.questionholder);
        imgholder = findViewById(R.id.imgholder);

        choice = new String[2];
        choice[0] = "date";
        choice[1] = "place";

        opts = new ArrayList<>();
        ansflags = new ArrayList<>();
        reactionTimes = new ArrayList<>();

        opt1 = findViewById(R.id.btn1);
        opt2 = findViewById(R.id.btn2);
        opt3 = findViewById(R.id.btn3);
        passbtn = findViewById(R.id.passbtn);
        Collections.shuffle(opts);


        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        Log.d(user.getUid(), "userid");
        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();

        memoriesRef = databaseReference.child("Memories").child(user.getUid());

        mQuery = memoriesRef.orderByChild("date");

        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    Memory memory = datas.getValue(Memory.class);
                    memories.add(memory);
                    Log.d(memory.getPeople(), "obje");
                }
                memocount = memories.size();
                Collections.shuffle(memories);
                memorieslist = memories;

                Random generator = new Random();
                int randomIndex = generator.nextInt(choice.length);
                if (choice[randomIndex].equals("date")) {
                    opt1.setText(memorieslist.get(2).getDate());
                    opt2.setText(memorieslist.get(0).getDate());
                    opt3.setText(memorieslist.get(1).getDate());

                    correctans = memorieslist.get(0).getDate();

                    question.setText("Yukarıdaki fotoğraf hangi tarihte çekildi?");
                } else {
                    opt1.setText(memorieslist.get(2).getPlace());
                    opt2.setText(memorieslist.get(0).getPlace());
                    opt3.setText(memorieslist.get(1).getPlace());

                    correctans = memorieslist.get(0).getPlace();

                    question.setText("Yukarıdaki fotoğraf nerede çekildi?");

                }


                storageRef = FirebaseStorage.getInstance().getReference();


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


                remainingmemos = memocount - 1;


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });


        Button.OnClickListener myOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickcount++;
                long endTime = SystemClock.elapsedRealtime();
                long elapsedTime = endTime - startTime;
                elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime);
                totalreacttime += elapsedSeconds;
                reactionTime = DateUtils.formatElapsedTime(elapsedSeconds);
                reactionTimes.add(reactionTime);

                Button btn = findViewById(v.getId());
                ans = btn.getText().toString();

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

                checkremaining(remainingmemos, clickcount);

            }

        };

        opt1.setOnClickListener(myOnClickListener);
        opt2.setOnClickListener(myOnClickListener);
        opt3.setOnClickListener(myOnClickListener);
        passbtn.setOnClickListener(myOnClickListener);
        startTime = SystemClock.elapsedRealtime();


    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MB_TestActivity_Part2.this);

        alert.setMessage("Testi iptal etmek istiyor musunuz?");


        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
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

    public void next() {
        memorieslist.remove(0);
        Random generator = new Random();
        Collections.shuffle(memorieslist);
        int randomIndex = generator.nextInt(choice.length);
        if (choice[randomIndex].equals("date")) {
            opt1.setText(memorieslist.get(2).getDate());
            opt2.setText(memorieslist.get(0).getDate());
            opt3.setText(memorieslist.get(1).getDate());

            correctans = memorieslist.get(0).getDate();

            question.setText("Yukarıdaki fotoğraf hangi yılda çekildi?");
        } else {
            opt1.setText(memorieslist.get(2).getPlace());
            opt2.setText(memorieslist.get(0).getPlace());
            opt3.setText(memorieslist.get(1).getPlace());

            correctans = memorieslist.get(0).getPlace();

            question.setText("Yukarıdaki fotoğraf nerede çekildi?");

        }

        storageRef.child("Users/" + memorieslist.get(0).getUserid() + "/Memories/" + memorieslist.get(0).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MB_TestActivity_Part2.this).load(uri.toString()).centerCrop().into(imgholder);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
            }
        });

        startTime = SystemClock.elapsedRealtime();

        remainingmemos--;

    }

    public void toResults() {
        long averagereacttime = totalreacttime / reactionTimes.size();
        String avg_reaction = DateUtils.formatElapsedTime(averagereacttime);


        File file = new File(getExternalFilesDir(null), "mbresult.txt");

        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));

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
        final String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        final StorageReference fileRef = storageRef.child("Users/" + user.getUid() + "/MemoryBox Results/" + timeStamp + ".txt");
        Uri fileuri = Uri.fromFile(file);

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
        file.delete();
        Intent intToHome = new Intent(MB_TestActivity_Part2.this, HomeActivity.class);
        startActivity(intToHome);

    }

    public void checkremaining(int rem, int ct) {
        if (remainingmemos < 3 || clickcount >= 8) {
            toResults();
        } else next();
    }

    public void saveFileUrlToDatabase(String fileurl) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("MemoryBoxResults").child(user.getUid()).child(fileurl).setValue(fileurl);

    }

}


