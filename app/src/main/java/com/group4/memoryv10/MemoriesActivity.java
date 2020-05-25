package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.EditText;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MemoriesActivity extends AppCompatActivity {
    String ctPin;
    DatabaseReference databaseReference, userRef, memoriesRef;
    FirebaseUser user;
    RecyclerView view;
    ImageAdapter mAdapter;
    private FirebaseAuth mAuth;
    private final String TAG = this.getClass().getName().toUpperCase();
    private List<Memory> mUploads;
    HashMap<Integer, Memory> map;
    TreeMap<Integer, Memory> orderedmap;

    //create action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_memories, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //when action bar menu item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                //create alert to get admin (caretaker) pin
                AlertDialog.Builder alert = new AlertDialog.Builder(MemoriesActivity.this);
                alert.setTitle("Dikkat");
                alert.setMessage("Yönetici şifresini giriniz!");

                //create an EditText view to get user input
                final EditText input = new EditText(MemoriesActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alert.setView(input);

                alert.setPositiveButton("Devam", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        //if input is equal to admin pin, redirect to Edit Memories Activity
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

        //create action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Anılarım");
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userRef = databaseReference.child("Users");
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

        view = findViewById(R.id.rcView);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this));

        //create an array list to keep the memories in the database
        mUploads = new ArrayList<>();
        memoriesRef = databaseReference.child("Memories").child(user.getUid());

        map = new HashMap<>();
        orderedmap = new TreeMap<>();

        //get memories from database
        memoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot datas: dataSnapshot.getChildren()) {
                    //get data as an object of memory class
                    Memory memory = datas.getValue(Memory.class);
                    //get each memory's year
                    String[] date = memory.getDate().split(" ");
                    Integer year = Integer.parseInt(date[1]);
                    //add memories to map
                    map.put(year,memory);
                    //count memories
                    count++;
                }
                //order memories by date (like a timeline)
                orderedmap.putAll(map);
                mUploads =  new ArrayList<Memory>((orderedmap.values()));
                Collections.reverse(mUploads);
                //create an adapter to display each memory
                mAdapter = new ImageAdapter(MemoriesActivity.this, mUploads);

                //attach adapter to activity's view (add card to recycler view)
                view.setAdapter(mAdapter);

                //if user has no memories
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

    //when action bar back button clicked, close activity
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onRestart() {
        //reload view
        super.onRestart();
    }

    @Override
    public void onResume()
    {
        //After a pause OR at startup, reload view
        super.onResume();
    }

    public void setCaretakerPin(String pin){
        ctPin = pin;
    }
}
