package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
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
    String memoryURL;
    String imgpeople;
    String imgdate;
    String imgplace;
    String timeStamp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Anı Ekle");
        }
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef= FirebaseStorage.getInstance().getReference();
        ch = (Button) findViewById(R.id.choosebutton);
        up = (Button) findViewById(R.id.uploadbutton);
        img = (ImageView) findViewById(R.id.imageView);
        people = (EditText)findViewById(R.id.peopleTxt);
        date = (EditText)findViewById(R.id.dateTxt);
        place = (EditText)findViewById(R.id.placeTxt);

        checkFields(people);
        checkFields(date);
        checkFields(place);

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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private String getExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void Fileuploader(){
        user = mAuth.getCurrentUser();
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        setTimeStamp();
        final StorageReference imageRef=storageRef.child("Users/" + user.getUid() + "/Memories/" + getTimeStamp());
        imgpeople = people.getText().toString();
        imgdate = date.getText().toString();
        imgplace = place.getText().toString();


        imageRef.putFile(imgURL)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                memoryURL = uri.toString();

                            }
                        });
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        saveImageUrlToDatabase(user.getUid(),getTimeStamp(),imgpeople,imgdate,imgplace);

                        Toast.makeText(AddMemoryActivity.this,"Fotoğraf başarıyla yüklendi.",Toast.LENGTH_LONG).show();
                        reloadActivity();
                        onBackPressed();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });



    }

    private void FileChooser(){
        Intent intent=new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgURL=data.getData();
            img.setImageURI(imgURL);

        }
    }

    public void saveImageUrlToDatabase(String userid, String memoURL, String people, String date, String place){
        Memory memory = new Memory(userid,memoURL,people,date,place);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Memories").child(user.getUid()).child(memoURL).setValue(memory);

    }
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
    }

    public void reloadActivity(){
        this.recreate();
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

    public void checkFields(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("Bu alan boş bırakılamaz.");
        }
    }


}
