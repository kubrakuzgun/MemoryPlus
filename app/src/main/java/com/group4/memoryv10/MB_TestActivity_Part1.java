package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;


public class MB_TestActivity_Part1 extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference databaseReference, memoriesRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    StorageReference storageRef;
    private final String TAG = this.getClass().getName().toUpperCase();
    private List<Memory> memories;
    String timeStamp;

    private List<Memory> allmemories;
    private List<Memory> randommemories;
    private List<Memory> memorieslist;

    long startTime, elapsedSeconds;
    String reactionTime;

    ArrayList<ImageView> imgs;
    ArrayList<TextView> opts;
    ArrayList<String> answers, answerkey, ansflags;
    Query mQuery;
    int memocount, remainingmemos, clickcount, truecount, falsecount, passcount;
    ImageView img1, img2, img3, img4;
    TextView opt1, opt2, opt3, opt4;
    TextView target1, target2, target3, target4;
    String ans1, ans2, ans3, ans4, ans5, ans6, ans7, ans8;
    String correctans1, correctans2, correctans3, correctans4, correctans5, correctans6, correctans7, correctans8;


    public List<Memory> getAllmemories() {
        return allmemories;
    }

    public void setAllmemories(List<Memory> allmemories) {
        this.allmemories = allmemories;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mb_test_part1);
        randommemories = new ArrayList<>();
        memories = new ArrayList<>();
        imgs = new ArrayList<>();
        opts = new ArrayList<>();
        answers = new ArrayList<>();
        answerkey = new ArrayList<>();
        ansflags = new ArrayList<>();

        target1 = findViewById(R.id.target1);
        target2 = findViewById(R.id.target2);
        target3 = findViewById(R.id.target3);
        target4 = findViewById(R.id.target4);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);

        opt1 = findViewById(R.id.opt1);
        opt2 = findViewById(R.id.opt2);
        opt3 = findViewById(R.id.opt3);
        opt4 = findViewById(R.id.opt4);

        imgs.add(img1);
        imgs.add(img2);
        imgs.add(img3);
        imgs.add(img4);
        opts.add(opt1);
        opts.add(opt2);
        opts.add(opt3);


        Collections.shuffle(imgs);
        Collections.shuffle(opts);

        target1.setText("");
        target2.setText("");
        target3.setText("");
        target4.setText("");

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

                opt1.setText(memorieslist.get(3).getPeople());
                opt2.setText(memorieslist.get(1).getPeople());
                opt3.setText(memorieslist.get(2).getPeople());
                opt4.setText(memorieslist.get(0).getPeople());

                correctans1 = memorieslist.get(0).getPeople();
                correctans2 = memorieslist.get(1).getPeople();
                correctans3 = memorieslist.get(2).getPeople();
                correctans4 = memorieslist.get(3).getPeople();

                answerkey.add(correctans1);
                answerkey.add(correctans2);
                answerkey.add(correctans3);
                answerkey.add(correctans4);

                storageRef= FirebaseStorage.getInstance().getReference();


                storageRef.child("Users/" + memorieslist.get(0).getUserid() + "/Memories/" + memorieslist.get(0).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(MB_TestActivity_Part1.this).load(uri.toString()).centerCrop().into(img1);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // File not found
                    }
                });

                storageRef.child("Users/" + memorieslist.get(1).getUserid() + "/Memories/" + memorieslist.get(1).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(MB_TestActivity_Part1.this).load(uri.toString()).centerCrop().into(img2);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // File not found
                    }
                });

                storageRef.child("Users/" + memorieslist.get(2).getUserid() + "/Memories/" + memorieslist.get(2).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(MB_TestActivity_Part1.this).load(uri.toString()).centerCrop().into(img3);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // File not found
                    }
                });

                storageRef.child("Users/" + memorieslist.get(3).getUserid() + "/Memories/" + memorieslist.get(3).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(MB_TestActivity_Part1.this).load(uri.toString()).centerCrop().into(img4);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // File not found
                    }
                });

                remainingmemos=memocount-4;




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });


        TextView.OnTouchListener myOnTouchListener_o1 = new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", opt1.getText());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                v.setVisibility(View.VISIBLE);
                return false;
            }
        };

        TextView.OnTouchListener myOnTouchListener_o2 = new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", opt2.getText());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                v.setVisibility(View.VISIBLE);
                return false;
            }
        };

        TextView.OnTouchListener myOnTouchListener_o3 = new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", opt3.getText());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                v.setVisibility(View.VISIBLE);
                return false;
            }
        };

        TextView.OnTouchListener myOnTouchListener_o4 = new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData data = ClipData.newPlainText("", opt4.getText());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                v.setVisibility(View.VISIBLE);

                return false;
            }
        };


        opt1.setOnTouchListener(myOnTouchListener_o1);
        opt2.setOnTouchListener(myOnTouchListener_o2);
        opt3.setOnTouchListener(myOnTouchListener_o3);
        opt4.setOnTouchListener(myOnTouchListener_o4);


        final int lightpurple = Color.parseColor("#9985B6");


        TextView.OnDragListener myOnDragListener_t1 = new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.getBackground().setColorFilter(lightpurple, PorterDuff.Mode.SRC_IN);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.getBackground().clearColorFilter();
                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String dragData = item.getText().toString();
                        v.getBackground().clearColorFilter();
                        if(target1.length()>1){
                            v.invalidate();
                        }
                        else {
                            target1.setText(dragData);
                            View view = (View) event.getLocalState();
                            view.setVisibility(View.INVISIBLE);
                        }                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.getBackground().clearColorFilter();

                    default:
                        break;
                }
                return true;
            }

        };


        TextView.OnDragListener myOnDragListener_t2 = new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.getBackground().setColorFilter(lightpurple, PorterDuff.Mode.SRC_IN);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.getBackground().clearColorFilter();
                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String dragData = item.getText().toString();
                        v.getBackground().clearColorFilter();
                        if(target2.length()>1){
                            v.invalidate();
                        }
                        else {
                            target2.setText(dragData);
                            View view = (View) event.getLocalState();
                            view.setVisibility(View.INVISIBLE);
                        }                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:

                        // Ignore the event
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        v.getBackground().clearColorFilter();

                    default:
                        break;
                }
                return true;
            }

        };

        TextView.OnDragListener myOnDragListener_t3 = new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.getBackground().setColorFilter(lightpurple, PorterDuff.Mode.SRC_IN);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.getBackground().clearColorFilter();
                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String dragData = item.getText().toString();
                        v.getBackground().clearColorFilter();
                        if(target3.length()>1){
                            v.invalidate();
                        }
                        else {
                            target3.setText(dragData);
                            View view = (View) event.getLocalState();
                            view.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.getBackground().clearColorFilter();

                    default:
                        break;
                }
                return true;
            }

        };

        TextView.OnDragListener myOnDragListener_t4 = new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:

                        v.getBackground().setColorFilter(lightpurple, PorterDuff.Mode.SRC_IN);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.getBackground().clearColorFilter();
                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String dragData = item.getText().toString();
                        v.getBackground().clearColorFilter();
                        if(target4.length()>1){
                            v.invalidate();
                        }
                        else {
                            target4.setText(dragData);
                            View view = (View) event.getLocalState();
                            view.setVisibility(View.INVISIBLE);
                        }

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.getBackground().clearColorFilter();



                    default:
                        break;
                }
                return true;
            }

        };


        target1.setOnDragListener(myOnDragListener_t1);
        target2.setOnDragListener(myOnDragListener_t2);
        target3.setOnDragListener(myOnDragListener_t3);
        target4.setOnDragListener(myOnDragListener_t4);

        startTime = SystemClock.elapsedRealtime();



    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MB_TestActivity_Part1.this);

        alert.setMessage("Testi iptal etmek istiyor musunuz?");


        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                    Intent cancelint = new Intent(MB_TestActivity_Part1.this, HomeActivity.class);
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

    public void next(View v){
        answers.add(target1.getText().toString());
        answers.add(target2.getText().toString());
        answers.add(target3.getText().toString());
        answers.add(target4.getText().toString());
        clickcount++;
        if(remainingmemos<4 || clickcount>1){
            toNextPart();
        }
        else{

            opt1.setText(memorieslist.get(6).getPeople());
            opt2.setText(memorieslist.get(5).getPeople());
            opt3.setText(memorieslist.get(4).getPeople());
            opt4.setText(memorieslist.get(7).getPeople());
            opt1.setVisibility(View.VISIBLE);
            opt2.setVisibility(View.VISIBLE);
            opt3.setVisibility(View.VISIBLE);
            opt4.setVisibility(View.VISIBLE);

            target1.setText("");
            target2.setText("");
            target3.setText("");
            target4.setText("");

            correctans5 = memorieslist.get(4).getPeople();
            correctans6 = memorieslist.get(5).getPeople();
            correctans7 = memorieslist.get(6).getPeople();
            correctans8 = memorieslist.get(7).getPeople();

            answerkey.add(correctans5);
            answerkey.add(correctans6);
            answerkey.add(correctans7);
            answerkey.add(correctans8);

            storageRef= FirebaseStorage.getInstance().getReference();


            storageRef.child("Users/" + memorieslist.get(4).getUserid() + "/Memories/" + memorieslist.get(4).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(MB_TestActivity_Part1.this).load(uri.toString()).centerCrop().into(img1);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // File not found
                }
            });

            storageRef.child("Users/" + memorieslist.get(5).getUserid() + "/Memories/" + memorieslist.get(5).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(MB_TestActivity_Part1.this).load(uri.toString()).centerCrop().into(img2);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // File not found
                }
            });

            storageRef.child("Users/" + memorieslist.get(6).getUserid() + "/Memories/" + memorieslist.get(6).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(MB_TestActivity_Part1.this).load(uri.toString()).centerCrop().into(img3);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // File not found
                }
            });

            storageRef.child("Users/" + memorieslist.get(7).getUserid() + "/Memories/" + memorieslist.get(7).getMemoURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(MB_TestActivity_Part1.this).load(uri.toString()).centerCrop().into(img4);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // File not found
                }
            });

            remainingmemos -= 4;

        }
    }

    public void toNextPart(){
        savePart1Results();
        Intent nextint = new Intent(MB_TestActivity_Part1.this, MB_TestActivity_Part2.class);
        startActivity(nextint);
    }

    public void savePart1Results(){
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        long endTime = SystemClock.elapsedRealtime();
        long elapsedTime = endTime - startTime;
        elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime);
        // elapsedSeconds = elapsedTime / 1000;

        reactionTime = DateUtils.formatElapsedTime(elapsedSeconds);

        for(int i=0; i<8; i++){
            if(answers.get(i).equals(answerkey.get(i))){
                ansflags.add(i,"doğru");
                truecount++;
            }
            else if(answers.get(i).equals("")){
                ansflags.add(i,"pas");
                passcount++;
            }
            else {
                ansflags.add(i,"yanlış");
                falsecount++;
            }

        }


        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(MB_TestActivity_Part1.this,"Depolama alanına erişilemiyor.",Toast.LENGTH_LONG).show();
        }



        File file = new File(getExternalFilesDir(null), "mbresult.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));

            buf.append("Bölüm 1: Fotoğraf-Kişi/Nesne eşleştirme");
            buf.newLine();
            buf.newLine();
            buf.append("Toplam Soru Sayısı:  ").append(String.valueOf(answers.size()));
            buf.newLine();
            buf.append("Doğru Cevap Sayısı:  ").append(String.valueOf(truecount));
            buf.newLine();
            buf.append("Yanlış Cevap Sayısı:  ").append(String.valueOf(falsecount));
            buf.newLine();
            buf.append("Boş Cevap Sayısı:  ").append(String.valueOf(passcount));
            buf.newLine();
            buf.append("Reaksiyon Süresi:  ").append(String.valueOf(reactionTime));
            buf.newLine();
            buf.newLine();
            buf.newLine();


            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
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
