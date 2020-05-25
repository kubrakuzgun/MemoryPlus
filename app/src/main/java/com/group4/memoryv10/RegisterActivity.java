package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    EditText name, surname, email, password, confirmpsw;
    Button registerButton;
    TextView loginTxt;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    public static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.memberEmail);
        password = findViewById(R.id.memberPassword);
        confirmpsw = findViewById(R.id.confirmpsw);
        registerButton = findViewById(R.id.registerButton);
        loginTxt = findViewById(R.id.loginTxt);
        firebaseAuth = FirebaseAuth.getInstance();

        //when register button clicked
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check empty inputs
                checkFields(name);
                checkFields(surname);
                checkFields(email);
                checkFields(password);
                checkFields(confirmpsw);

                //convert fields to string
                String uemail = email.getText().toString().trim();
                String upassword = password.getText().toString().trim();
                String confrimpass = confirmpsw.getText().toString().trim();

                //make uname and usurname final to use in static content
                 final String uname = name.getText().toString().trim();
                 final String usurname = surname.getText().toString().trim();

                if (!TextUtils.isEmpty(uemail) && (!TextUtils.isEmpty(upassword)) && (!TextUtils.isEmpty(uname)) && (!TextUtils.isEmpty(usurname))){
                    //check password length
                    if(upassword.length()<6){
                        password.setError("Şifre en az 6 karakter olmalıdır.");
                    }
                    else if(!upassword.equals(confrimpass)){
                        confirmpsw.setError("Şifreler uyuşmuyor.");
                    }
                    else {
                        firebaseAuth.createUserWithEmailAndPassword(uemail, upassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //if there is an error due to firebaseAuth
                                if(!task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this,"Geçersiz e-posta ya da şifre, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this,"Kayıt başarılı.",Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    //save user to database
                                    writeNewUser(user.getUid(), uname, usurname, 0, " ", " ", " ", " ", 0, " ", " ", 1234);
                                    createCaretakerPin();
                                    startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                                }
                            }
                        });
                    }
                }
                //if there is an error due to database or Internet connection
                else{
                    Toast.makeText(RegisterActivity.this,"Beklenmedik bir hata oluştu.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //redirect to Login
    public void openLoginActivity(View w){
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    //create user object and save to database
    private void writeNewUser(String userId, String name, String surname, int age, String address, String phone, String disease, String diseaseStage, int mmseScore, String caretaker, String caretakerPhone, int caretakerPin) {
        User user = new User(userId, name, surname, age, address, phone, disease, diseaseStage, mmseScore, caretaker, caretakerPhone, caretakerPin);
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Users").child(userId).setValue(user);
    }

    //check empty inputs
    public void checkFields(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("Bu alan boş bırakılamaz.");
        }
    }

    public void createCaretakerPin(){
        android.app.AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
        LinearLayout ll = new LinearLayout(RegisterActivity.this);

        final EditText inputpin = new EditText(RegisterActivity.this);
        inputpin.setInputType(InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputpin.setHint("Yönetici şifresi");
        checkFields(inputpin);

        final EditText inputcpin = new EditText(RegisterActivity.this);
        inputcpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputcpin.setHint("Şifreyi onayla");
        checkFields(inputcpin);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(inputpin);
        ll.addView(inputcpin);

        alert.setTitle("Yönetici şifresi oluştur");
        alert.setView(ll);
        alert.setPositiveButton("Oluştur", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pin = inputpin.getText().toString().trim();
                String cpin = inputcpin.getText().toString().trim();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentuser = auth.getCurrentUser();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                if(pin.length()>=4 && cpin.length()>=4){
                    if(pin.equals(cpin)){
                        dbRef.child("Users").child(currentuser.getUid()).child("caretakerPin").setValue(pin);
                        Toast.makeText(RegisterActivity.this, "Yönetici şifresi oluşturuldu", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Şifreler eşleşmiyor, lütfen tekrar deneyin..", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(RegisterActivity.this, "Şifre en az 4 karakter olmalıdır.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
