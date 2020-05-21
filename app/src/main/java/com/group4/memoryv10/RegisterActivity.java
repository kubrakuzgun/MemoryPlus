package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText name, surname, email, password;
    Button registerButton;
    TextView loginTxt;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;

    public static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        email = (EditText) findViewById(R.id.memberEmail);
        password = (EditText) findViewById(R.id.memberPassword);
        registerButton = (Button) findViewById(R.id.registerButton);
        loginTxt = (TextView) findViewById(R.id.loginTxt);

        checkFields(name);
        checkFields(surname);
        checkFields(email);
        checkFields(password);


        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uemail = email.getText().toString().trim();
                String upassword = password.getText().toString().trim();
                final String uname = name.getText().toString().trim();
                final String usurname = surname.getText().toString().trim();



                if(TextUtils.isEmpty(uname)){
                    name.setError("Bu alan boş bırakılamaz.");
                    name.requestFocus();
                }
                else if(TextUtils.isEmpty(usurname)){
                    surname.setError("Bu alan boş bırakılamaz.");
                    surname.requestFocus();
                }
                else if(TextUtils.isEmpty(uemail)){
                    email.setError("Bu alan boş bırakılamaz.");
                    email.requestFocus();
                }
                else  if(TextUtils.isEmpty(upassword)){
                    password.setError("Bu alan boş bırakılamaz.");
                    password.requestFocus();
                }


                else if (!TextUtils.isEmpty(uemail) && (!TextUtils.isEmpty(upassword)) && (!TextUtils.isEmpty(uname)) && (!TextUtils.isEmpty(usurname))){
                    if(upassword.length()<6){
                        Toast.makeText(RegisterActivity.this,"Şifre en az 6 karakter olmalıdır.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        firebaseAuth.createUserWithEmailAndPassword(uemail, upassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this,"Kayıt başarısız, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this,"Kayıt başarılı.",Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    writeNewUser(user.getUid(), uname, usurname, 0, " ", " ", " ", " ", 0, " ", " ", 1234);
                                    startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                                }
                            }
                        });
                    }

                }
                else{
                    Toast.makeText(RegisterActivity.this,"Beklenmedik bir hata oluştu.",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void openLoginActivity(View w){
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }


    private void writeNewUser(String userId, String name, String surname, int age, String address, String phone, String disease, String diseaseStage, int mmseScore, String caretaker, String caretakerPhone, int caretakerPin) {
        User user = new User(userId, name, surname, age, address, phone, disease, diseaseStage, mmseScore, caretaker, caretakerPhone, caretakerPin);
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Users").child(userId).setValue(user);

    }

    public void checkFields(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("Bu alan boş bırakılamaz.");
        }
    }


}
