package com.group4.memoryv10;

import androidx.appcompat.app.ActionBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class EditMemoriesActivity extends AppCompatActivity implements EditImageAdapter.OnItemClickListener {

    StorageReference storageRef;
    DatabaseReference databaseReference, memoriesRef;
    int memoryCount;
    private final String TAG = this.getClass().getName().toUpperCase();
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String userid;
    private List<Memory> mUploads;
    RecyclerView view;
    EditImageAdapter mAdapter;
    Query mQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memories);

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
        view = (RecyclerView) findViewById(R.id.viewRC);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();
        mQuery = memoriesRef.orderByChild("date");


        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();

                for (DataSnapshot datas: dataSnapshot.getChildren()) {

                    memoryCount++;
                    Memory upload = datas.getValue(Memory.class);
                    upload.setKey(datas.getKey());
                    mUploads.add(upload);

                }
                mAdapter = new EditImageAdapter(EditMemoriesActivity.this, mUploads);
                mAdapter.setOnItemClickListener(EditMemoriesActivity.this);
                view.setAdapter(mAdapter);



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

    }

    @Override
    public void onEditClick(int position) {
        Memory selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        memoriesRef = databaseReference.child("Memories").child(user.getUid());

        AlertDialog.Builder alert = new AlertDialog.Builder(EditMemoriesActivity.this);


        LinearLayout ll = new LinearLayout(this);

// Set an EditText view to get user input
        final EditText inputpeople = new EditText(EditMemoriesActivity.this);
        inputpeople.setInputType(InputType.TYPE_CLASS_TEXT);
        inputpeople.setText(selectedItem.getPeople());

        final EditText inputdate = new EditText(EditMemoriesActivity.this);
        inputdate.setInputType(InputType.TYPE_CLASS_TEXT);
        inputdate.setText(selectedItem.getDate());

        final EditText inputplace = new EditText(EditMemoriesActivity.this);
        inputplace.setInputType(InputType.TYPE_CLASS_TEXT);
        inputplace.setText(selectedItem.getPlace());

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(inputpeople);
        ll.addView(inputdate);
        ll.addView(inputplace);
        alert.setTitle("Anıyı Düzenle");

        alert.setView(ll);


        alert.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                checkFields(inputpeople);
                checkFields(inputdate);
                checkFields(inputplace);


                String newpeople = inputpeople.getText().toString().trim();
                String newdate = inputdate.getText().toString().trim();
                String newplace = inputplace.getText().toString().trim();

                memoriesRef.child(selectedKey).child("people").setValue(newpeople);
                memoriesRef.child(selectedKey).child("date").setValue(newdate);
                memoriesRef.child(selectedKey).child("place").setValue(newplace);

                Toast.makeText(EditMemoriesActivity.this, "Anı düzenlendi.", Toast.LENGTH_SHORT).show();
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

        final Memory selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        final StorageReference imageRef=storageRef.child("Users/" + user.getUid() + "/Memories/" + selectedItem.getMemoURL());

        AlertDialog.Builder alert = new AlertDialog.Builder(EditMemoriesActivity.this);
        alert.setMessage("Bu anıyı silmek istiyor musunuz?");


        alert.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

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

    public void reloadActivity(){
        this.recreate();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        reloadActivity();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

    }

    public void checkFields(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("Bu alan boş bırakılamaz.");
        }
    }



}
