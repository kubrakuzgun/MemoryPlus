package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button loginButton;
    TextView registerButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //attach xml layout to activity
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.memberEmail);
        password = findViewById(R.id.memberPassword);
        registerButton = findViewById(R.id.newmemberButton);
        loginButton = findViewById(R.id.registerButton);
        //get all authentication info
        firebaseAuth = FirebaseAuth.getInstance();

        //when login button pressed
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if email and password are not empty
                if (checkEmail(email) &&  checkPassword(password)){
                    String uemail = email.getText().toString();
                    String upassword = password.getText().toString();
                    //login with email and password input
                    firebaseAuth.signInWithEmailAndPassword(uemail, upassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if email or password is wrong
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"E-posta adresi ya da şifre hatalı.",Toast.LENGTH_SHORT).show();
                            }
                            //if email and password are correct
                            else{
                                Toast.makeText(LoginActivity.this,"Giriş başarılı.",Toast.LENGTH_SHORT).show();
                                Intent intToHome = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intToHome);
                            }
                        }
                    });
                }
                //error due to database or Internet connection
                else{
                    Toast.makeText(LoginActivity.this,"Beklenmedik bir hata oluştu.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //redirect to register activity
    public void openRegisterActivity(View w){
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    //close app when navigation back pressed
    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    //check if email input is empty
    public boolean checkEmail(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("E-posta adresinizi giriniz.");
            return false;
        }
        else return true;
    }

    //check if password input is empty
    public boolean checkPassword(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("Şifrenizi giriniz.");
            return false;
        }
        else return true;
    }
}
