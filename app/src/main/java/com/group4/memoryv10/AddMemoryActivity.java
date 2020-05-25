package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.concurrent.TimeUnit;

public class AddMemoryActivity extends AppCompatActivity {
    Button ch, up;
    ImageView img;
    EditText people,date,place;
    StorageReference storageRef;
    DatabaseReference databaseReference;
    public Uri imgURL;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    String imgpeople;
    String imgdate;
    String imgplace;
    String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

        //create action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Anı Ekle");
        }

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef= FirebaseStorage.getInstance().getReference();

        ch = findViewById(R.id.choosebutton);   //choose button
        up = findViewById(R.id.uploadbutton);   //upload button
        img = findViewById(R.id.imageView);
        people = findViewById(R.id.peopleTxt);
        date = findViewById(R.id.dateTxt);
        place = findViewById(R.id.placeTxt);

        //set click listeners to choose and upload buttons
        ch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FileChooser();
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fileuploader();
            }
        });
    }

    //when action bar back button clicked
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //function to upload photo to firebase storage
    private void Fileuploader(){
        //check empty inputs
        checkFields(people);
        checkFields(date);
        checkFields(place);

        user = mAuth.getCurrentUser();
        //get current time
        setTimeStamp();
        //timestamp is used as the photo's name to be unique
        final StorageReference imageRef = storageRef.child("Users/" + user.getUid() + "/Memories/" + getTimeStamp());
        imgpeople = people.getText().toString();
        imgdate = date.getText().toString();
        imgplace = place.getText().toString();

        //upload file to firebase storage
        imageRef.putFile(imgURL)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // OK
                            }
                        });

                        //save photo's url to users database
                        saveImageUrlToDatabase(user.getUid(),getTimeStamp(),imgpeople,imgdate,imgplace);

                        Toast.makeText(AddMemoryActivity.this,"Fotoğraf başarıyla yüklendi.",Toast.LENGTH_LONG).show();

                        //reload view to display changes
                        reloadActivity();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(AddMemoryActivity.this,"Fotoğrafı yüklerken bir hata oluştu.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    //function to choose photo from device
    private void FileChooser(){
        Intent intent=new Intent();
        //set file type
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    //check if photo is retrieved from device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgURL=data.getData();
            img.setImageURI(imgURL);
        }
        else{
            Toast.makeText(AddMemoryActivity.this,"Beklenmedik bir hata oluştu.",Toast.LENGTH_LONG).show();
        }
    }

    //function to save photo's url to users database
    public void saveImageUrlToDatabase(String userid, String memoURL, String people, String date, String place){
        Memory memory = new Memory(userid,memoURL,people,date,place);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Memories").child(user.getUid()).child(memoURL).setValue(memory);
    }

    //get time stamp
    public String getTimeStamp() {
        return timeStamp;
    }

    //set current time
    public void setTimeStamp() {
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
    }

    //reload activity to display changes
    public void reloadActivity(){
        this.recreate();
    }

    //reload view
    @Override
    public void onRestart() {
        super.onRestart();
    }

    //reload view
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
