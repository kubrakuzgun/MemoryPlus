package com.group4.memoryv10;

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
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class EditMemoriesActivity extends AppCompatActivity implements EditImageAdapter.OnItemClickListener {
    StorageReference storageRef;
    DatabaseReference databaseReference, memoriesRef;
    int memoryCount;
    private final String TAG = this.getClass().getName().toUpperCase();
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private List<Memory> mUploads;
    RecyclerView view;
    EditImageAdapter mAdapter;
    HashMap<Integer, Memory> map;
    TreeMap<Integer, Memory> orderedmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memories);

        //create action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Düzenle");
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef= FirebaseStorage.getInstance().getReference();
        memoriesRef = databaseReference.child("Memories").child(user.getUid());

        view = findViewById(R.id.viewRC);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this));

        //create an array list to keep the memories in the database
        mUploads = new ArrayList<>();
        map = new HashMap<>();
        orderedmap = new TreeMap<>();


        //get memories from database
        memoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //reset memories array list
                mUploads.clear();

                for (DataSnapshot datas: dataSnapshot.getChildren()) {
                    //count memories
                    memoryCount++;
                    Memory upload = datas.getValue(Memory.class);
                    upload.setKey(datas.getKey());
                    //get each memory's year
                    String[] date = upload.getDate().split(" ");
                    Integer year = Integer.parseInt(date[1]);
                    //add memories to map
                    map.put(year,upload);
                }
                //order memories by date (like a timeline)
                orderedmap.putAll(map);
                mUploads =  new ArrayList<Memory>((orderedmap.values()));
                Collections.reverse(mUploads);

                //create an adapter to display each memory
                mAdapter = new EditImageAdapter(EditMemoriesActivity.this, mUploads);

                //attach adapter to activity's view
                mAdapter.setOnItemClickListener(EditMemoriesActivity.this);
                view.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    //when action bar back button clicked
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //redirect to add memory activity
    public void toAddInt(View w){
        if(memoryCount<30){
            Intent addint = new Intent(EditMemoriesActivity.this, AddMemoryActivity.class);
            startActivity(addint);
        }
        else{
            Toast.makeText(EditMemoriesActivity.this,"En fazla 30 anı ekleyebilirsiniz.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        //implemented method from interface
    }

    @Override
    public void onEditClick(int position) {
        //get clicked memory's position and key
        Memory selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        //create alert to edit memory
        AlertDialog.Builder alert = new AlertDialog.Builder(EditMemoriesActivity.this);

        //create a linear layout to insert into alert
        LinearLayout ll = new LinearLayout(this);

        //create an EditText view to get user input
        final EditText inputpeople = new EditText(EditMemoriesActivity.this);
        inputpeople.setInputType(InputType.TYPE_CLASS_TEXT);
        inputpeople.setText(selectedItem.getPeople());

        final EditText inputdate = new EditText(EditMemoriesActivity.this);
        inputdate.setInputType(InputType.TYPE_CLASS_TEXT);
        inputdate.setText(selectedItem.getDate());

        final EditText inputplace = new EditText(EditMemoriesActivity.this);
        inputplace.setInputType(InputType.TYPE_CLASS_TEXT);
        inputplace.setText(selectedItem.getPlace());

        //linear layout attributes
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(inputpeople);
        ll.addView(inputdate);
        ll.addView(inputplace);
        alert.setTitle("Anıyı Düzenle");
        alert.setView(ll);

        alert.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //check if inputs are empty
                checkFields(inputpeople);
                checkFields(inputdate);
                checkFields(inputplace);

                String newpeople = inputpeople.getText().toString().trim();
                String newdate = inputdate.getText().toString().trim();
                String newplace = inputplace.getText().toString().trim();

                //update database
                memoriesRef.child(selectedKey).child("people").setValue(newpeople);
                memoriesRef.child(selectedKey).child("date").setValue(newdate);
                memoriesRef.child(selectedKey).child("place").setValue(newplace);

                Toast.makeText(EditMemoriesActivity.this, "Anı düzenlendi.", Toast.LENGTH_SHORT).show();

                //reload activity to display changes
                reloadActivity();
                }

        });

        alert.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    @Override
    public void onDeleteClick(int position) {
        //get selected memory's position and key
        final Memory selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        //get firebase storage reference of memory photo
        final StorageReference imageRef=storageRef.child("Users/" + user.getUid() + "/Memories/" + selectedItem.getMemoURL());

        //create alert to confirm delete
        AlertDialog.Builder alert = new AlertDialog.Builder(EditMemoriesActivity.this);
        alert.setMessage("Bu anıyı silmek istiyor musunuz?");

        alert.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //delete memory
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        memoriesRef = databaseReference.child("Memories").child(user.getUid());
                        memoriesRef.child(selectedKey).removeValue();
                        Toast.makeText(EditMemoriesActivity.this, "Anı silindi.", Toast.LENGTH_SHORT).show();
                        reloadActivity();
                    }
                });
            }

        });

        alert.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    //reload activity to display changes
    public void reloadActivity(){
        this.recreate();
    }

    @Override
    public void onRestart() {
        //reload view
        super.onRestart();
        //call reloadActivity function recreate whole activity after changes made
        reloadActivity();
    }

    @Override
    public void onResume()
    {
        //After a pause OR at startup, reload view
        super.onResume();

    }

    //check empty inputs
    public void checkFields(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("Bu alan boş bırakılamaz.");
        }
    }
}
