package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.List;

public class MemoriesActivity extends AppCompatActivity {
    String ctPin;
    DatabaseReference databaseReference, userRef, memoriesRef;
    FirebaseUser user;
    RecyclerView view;
    ImageAdapter mAdapter;
    private FirebaseAuth mAuth;
    private final String TAG = this.getClass().getName().toUpperCase();
    private List<Memory> mUploads;
    Query mQuery;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_memories, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                AlertDialog.Builder alert = new AlertDialog.Builder(MemoriesActivity.this);

                alert.setTitle("Dikkat");
                alert.setMessage("Yönetici şifresini giriniz!");

// Set an EditText view to get user input
                final EditText input = new EditText(MemoriesActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alert.setView(input);

                alert.setPositiveButton("Devam", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        if(value.equals(ctPin)){

                            Intent editint = new Intent(MemoriesActivity.this, EditMemoriesActivity.class);
                            startActivity(editint);


                        }
                        else{
                            Toast.makeText(MemoriesActivity.this,"Hatalı şifre.",Toast.LENGTH_SHORT).show();

                            dialog.cancel();
                        }
                    }
                });

                alert.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.show();
                return true;



            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Anılarım");
        }



        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userRef = databaseReference.child("Users");
        memoriesRef = databaseReference.child("Memories").child(user.getUid());


        Log.v("USERID", userRef.getKey());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String caretaker_pin;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas: dataSnapshot.getChildren()) {
                    if(datas.child("id").getValue().equals(user.getUid())){
                        caretaker_pin = datas.child("caretakerPin").getValue().toString();
                        break;
                    }
                }
                setCaretakerPin(caretaker_pin);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }


        });


        ArrayList<String> arr = new ArrayList<String>();

        view = (RecyclerView) findViewById(R.id.rcView);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this));


        mUploads = new ArrayList<>();
        mQuery = memoriesRef.orderByChild("date");

        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot datas: dataSnapshot.getChildren()) {

                    Memory memory = datas.getValue(Memory.class);
                    mUploads.add(memory);
                    count++;

                }

                mAdapter = new ImageAdapter(MemoriesActivity.this, mUploads);

                view.setAdapter(mAdapter);

                if(count==0){
                    Toast.makeText(MemoriesActivity.this,"Hiç anı yüklemediniz.",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });


    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    public void setCaretakerPin(String pin){
        ctPin = pin;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

    }




}
