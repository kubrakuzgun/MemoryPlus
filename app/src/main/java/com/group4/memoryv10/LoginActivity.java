package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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


public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button loginButton;
    TextView registerButton;
    FirebaseAuth firebaseAuth;
    public static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.memberEmail);
        password = (EditText) findViewById(R.id.memberPassword);
        registerButton = (TextView) findViewById(R.id.newmemberButton);
        loginButton = (Button) findViewById(R.id.registerButton);

        firebaseAuth = FirebaseAuth.getInstance();

        checkEmail(email);
        checkPassword(password);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uemail = email.getText().toString();
                String upassword = password.getText().toString();

                if(TextUtils.isEmpty(uemail)){
                    email.setError("Lütfen eposta adresinizi girin.");
                    email.requestFocus();
                }
                else  if(TextUtils.isEmpty(upassword)){
                    password.setError("Lütfen şifrenizi girin.");
                    password.requestFocus();
                }

                else if ( (!TextUtils.isEmpty(uemail)) && (!TextUtils.isEmpty(upassword)) ){
                    firebaseAuth.signInWithEmailAndPassword(uemail, upassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Giriş başarısız, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Giriş başarılı.",Toast.LENGTH_SHORT).show();
                                Intent intToHome = new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intToHome);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Beklenmedik bir hata oluştu.",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void openRegisterActivity(View w){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    public void checkEmail(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("E-posta adresinizi giriniz.");
        }
    }

    public void checkPassword(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("Şifrenizi giriniz.");
        }
    }



}
