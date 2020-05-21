package com.group4.memoryv10;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends AppCompatActivity {
    StorageReference storageRef;
    DatabaseReference databaseReference, userRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    String ctPin, usrPsw;
    private final String TAG = this.getClass().getName().toUpperCase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();
            if(key.equals("logout")){
                new AlertDialog.Builder(getActivity()).setTitle("Oturumu Kapat")
                        .setMessage("Çıkış yapmak istiyor musunuz?")
                        .setPositiveButton("Onayla",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(loginIntent);
                                        getActivity().finish();
                                    }
                                })
                        .setNegativeButton("İptal", null).show();
                return true;
            }
            else if(key.equals("changeuserpassword")){
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                LinearLayout ll = new LinearLayout(getContext());

                final EditText oldpsw = new EditText(getContext());
                oldpsw.setInputType(InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                oldpsw.setHint("Şu anda kullandığınız şifre");

                final EditText inputpsw = new EditText(getContext());
                inputpsw.setInputType(InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputpsw.setHint("Yeni şifre");

                final EditText inputcpsw = new EditText(getContext());
                inputcpsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputcpsw.setHint("Yeni şifreyi onayla");

                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(oldpsw) ;
                ll.addView(inputpsw);
                ll.addView(inputcpsw);
                alert.setTitle("Şifre Güncelleme");

                alert.setView(ll);

                alert.setPositiveButton("Devam", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String psw = inputpsw.getText().toString();
                        String cpsw = inputcpsw.getText().toString();
                        String oldpass = oldpsw.getText().toString();
                        FirebaseAuth auth = FirebaseAuth.getInstance();


                        if(psw.length()>=6 && cpsw.length()>=6) {
                            if (psw.equals(cpsw)) {
                                final FirebaseUser currentuser = auth.getCurrentUser();
                                final String email = currentuser.getEmail();
                                AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

                                currentuser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            currentuser.updatePassword(psw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Hata! Şifre değiştirilemedi..", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Şifreniz başarıyla değiştirildi.", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Şifreniz yanlış", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Şifreler eşleşmiyor, lütfen tekrar deneyin..", Toast.LENGTH_SHORT).show();

                            }
                        }
                        else {
                            Toast.makeText(getContext(), "Şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                alert.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.show();

                return true;
            }

            else if(key.equals("changeadminpin")){

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                LinearLayout ll = new LinearLayout(getContext());

                final EditText inputpin = new EditText(getContext());
                inputpin.setInputType(InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputpin.setHint("Yeni yönetici şifresi");

                final EditText inputcpin = new EditText(getContext());
                inputcpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputcpin.setHint("Yeni şifreyi onayla");

                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(inputpin);
                ll.addView(inputcpin);
                alert.setTitle("Yönetici Şifresi Güncelleme");

                alert.setView(ll);

                alert.setPositiveButton("Devam", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String pin = inputpin.getText().toString().trim();
                        String cpin = inputcpin.getText().toString().trim();
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        FirebaseUser currentuser = auth.getCurrentUser();
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                        if(pin.length()>=4 && cpin.length()>=4){
                            if(pin.equals(cpin)){

                                dbRef.child("Users").child(currentuser.getUid()).child("caretakerPin").setValue(pin);
                                Toast.makeText(getContext(), "Yönetici şifresi değiştirildi", Toast.LENGTH_SHORT).show();

                            }
                            else{

                                Toast.makeText(getContext(), "Şifreler eşleşmiyor, lütfen tekrar deneyin..", Toast.LENGTH_SHORT).show();

                            }

                        }
                        else {

                            Toast.makeText(getContext(), "Şifre en az 4 karakter olmalıdır.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                alert.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.show();


                return true;
            }

            else return false;

        }
    }


    public void setCaretakerPin(String pin){
        ctPin = pin;
    }


}