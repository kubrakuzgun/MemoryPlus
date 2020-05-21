package com.group4.memoryv10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    DatabaseReference databaseReference, memoriesRef, userRef;
    int memoryCount;
    private final String TAG = this.getClass().getName().toUpperCase();
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String ctPin;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        memoriesRef = databaseReference.child("Memories").child(user.getUid());

        memoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas: dataSnapshot.getChildren()) {

                    memoryCount++;

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

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

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    Log.e(" ", "Permission Granted.");

                } else {
                    requestPermission(); // Code for permission
                }
            } else {
                Log.e(" ", "Error.");

            }
        }

    }


    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    public void toProfileIntent(View v){
        Intent profileint = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(profileint);
    }

    public void toSettingsIntent(View v){

        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);

        alert.setTitle("Dikkat");
        alert.setMessage("Yönetici şifresini giriniz!");

// Set an EditText view to get user input
        final EditText input = new EditText(HomeActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);

        alert.setPositiveButton("Devam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if(value.equals(ctPin)){
                    Intent settingsint = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(settingsint);

                }
                else{
                    Toast.makeText(HomeActivity.this,"Hatalı şifre.",Toast.LENGTH_SHORT).show();
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

    public void toMemoriesIntent(View v){
        Intent memoriesint = new Intent(HomeActivity.this, MemoriesActivity.class);
        startActivity(memoriesint);
    }

    public void toExercisesIntent(View v){
        Intent execisesint = new Intent(HomeActivity.this, GamesActivity.class);
        startActivity(execisesint);
    }

    public void toMemoboxIntent(View v){
        if(memoryCount>=5){
            Intent testsint = new Intent(HomeActivity.this, MemoryBoxActivity.class);
            startActivity(testsint);
        }
        else{
            Toast.makeText(HomeActivity.this,"Hafıza kutusu için az 5 anı eklemelisiniz.",Toast.LENGTH_SHORT).show();
        }

    }

    public void setCaretakerPin(String pin){
        ctPin = pin;
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(HomeActivity.this, "Sonuçlarınızın kaydedilebilmesi için cihazınızın depolama alanına erişim izni sağlamanız gerekmektedir.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }


}
