package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    ImageButton profilePicBtn;
    Button saveBtn;
    ImageView profilePic;
    public Uri picURL;
    String ctPin;
    private final String TAG = this.getClass().getName().toUpperCase();
    StorageReference storageRef;
    DatabaseReference databaseReference, userRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    EditText newname,newsurname,newage,newaddress,newphone,newdisease,newdiseasestage,newmmse,newcaretaker,newcaretakerphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //create action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Profilim");
        }

        profilePic = findViewById(R.id.profilePic);
        profilePicBtn = findViewById(R.id.editPic);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        storageRef= FirebaseStorage.getInstance().getReference();
        //get profile picture from storage and insert into image holder
        storageRef.child("Users/" + user.getUid() + "/ProfilePictures/profilepicture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileActivity.this).load(uri).into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
            }
        });

        //when edit profile picture button is clicked
        profilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               pictureChooser();
            }
        });

        saveBtn = findViewById(R.id.saveButton);
        newname = findViewById(R.id.nameTxt);
        newsurname =  findViewById(R.id.surnameTxt);
        //make uneditable
        newname.setKeyListener(null);
        newsurname.setKeyListener(null);

        newage = findViewById(R.id.ageTxt);
        newaddress = findViewById(R.id.addressTxt);
        newphone = findViewById(R.id.phoneTxt);
        newdisease = findViewById(R.id.diseaseTxt);
        newdiseasestage = findViewById(R.id.diseasestageTxt);
        newmmse = findViewById(R.id.mmsescoreTxt);
        newcaretaker = findViewById(R.id.caretakerTxt);
        newcaretakerphone = findViewById(R.id.caretakerphoneTxt);

        userRef = databaseReference.child("Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String name,surname,age,address,phone,disease,diseasestage,mmse,caretaker,caretakerphone,caretaker_pin;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas: dataSnapshot.getChildren()) {
                    //find current user from database
                    if(datas.child("id").getValue().equals(user.getUid())){
                        name=datas.child("name").getValue().toString();
                        surname=datas.child("surname").getValue().toString();
                        age=datas.child("age").getValue().toString();
                        address=datas.child("address").getValue().toString();
                        phone=datas.child("phone").getValue().toString();
                        disease=datas.child("disease").getValue().toString();
                        diseasestage=datas.child("diseaseStage").getValue().toString();
                        mmse=datas.child("mmseScore").getValue().toString();
                        caretaker=datas.child("caretaker").getValue().toString();
                        caretakerphone=datas.child("caretakerPhone").getValue().toString();
                        caretaker_pin = datas.child("caretakerPin").getValue().toString();
                        break;
                    }
                }
                newname.setText(name);
                newsurname.setText(surname);
                newage.setText(age);
                newaddress.setText(address);
                newphone.setText(phone);
                newdisease.setText(disease);
                newdiseasestage.setText(diseasestage);
                newmmse.setText(mmse);
                newcaretaker.setText(caretaker);
                newcaretakerphone.setText(caretakerphone);
                setCaretakerPin(caretaker_pin);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
                alert.setTitle("Dikkat");
                alert.setMessage("Yönetici şifresini giriniz!");
                //create an EditText view to get user input
                final EditText input = new EditText(ProfileActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alert.setView(input);
                alert.setPositiveButton("Devam", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        if(value.equals(ctPin)){
                            databaseReference.child("Users").child(user.getUid()).child("age").setValue(newage.getText().toString());
                            databaseReference.child("Users").child(user.getUid()).child("address").setValue(newaddress.getText().toString());
                            databaseReference.child("Users").child(user.getUid()).child("phone").setValue(newphone.getText().toString());
                            databaseReference.child("Users").child(user.getUid()).child("disease").setValue(newdisease.getText().toString());
                            databaseReference.child("Users").child(user.getUid()).child("diseaseStage").setValue(newdiseasestage.getText().toString());
                            databaseReference.child("Users").child(user.getUid()).child("mmseScore").setValue(newmmse.getText().toString());
                            databaseReference.child("Users").child(user.getUid()).child("caretaker").setValue(newcaretaker.getText().toString());
                            databaseReference.child("Users").child(user.getUid()).child("caretakerPhone").setValue(newcaretakerphone.getText().toString());

                            Toast.makeText(ProfileActivity.this,"Değişiklikler kaydedildi.",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            reloadActivity();
                        }
                        else{
                            Toast.makeText(ProfileActivity.this,"Hatalı şifre.",Toast.LENGTH_LONG).show();
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
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //function to select picture from device
    private void pictureChooser(){
        Intent intent=new Intent();
        //set file type to image
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //when picture is choosen, go to result
        startActivityForResult(intent,1);
    }

    //check picture choose result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if picture is successfully choosen (correct format)
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            picURL=data.getData();
            //set picture url
            profilePic.setImageURI(picURL);
            //upload picture to storage
            pictureUpload();
        }
    }

    //function to upload picture to storage
    public void pictureUpload(){
        user = mAuth.getCurrentUser();
        //create storage reference for picture
        StorageReference imageRef = storageRef.child("Users/" + user.getUid() + "/ProfilePictures/" + "profilepicture");
        //put picture url into storage
        imageRef.putFile(picURL)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(ProfileActivity.this,"Profil resmi başarıyla değiştirildi.",Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //Handle unsuccessful uploads
                        Toast.makeText(ProfileActivity.this,"Resim yüklenirken hata oluştu.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void setCaretakerPin(String pin){
        ctPin = pin;
    }

    //reload activity to display changes
    public void reloadActivity(){
        this.recreate();
    }

    @Override
    public void onRestart() {
        //reload view
        super.onRestart();
        reloadActivity();
        onBackPressed();
    }
    @Override
    public void onResume()
    {
        //After a pause OR at startup, reload view
        super.onResume();
    }
}
