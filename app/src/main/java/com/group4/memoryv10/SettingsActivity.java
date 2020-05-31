package com.group4.memoryv10;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.logging.Logger;

public class SettingsActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //create top action (tool) bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //create back button
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //get android studio settings fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    //when action bar back button pressed
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

        //when to a settings menu item clicked
        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            //get clicked item
            String key = preference.getKey();

            //if logout is clicked
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

            else if(key.equals("notifications")){
                final SwitchPreferenceCompat switchpref = (SwitchPreferenceCompat) preference;
                // SwitchPreference preference change listener
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        if(switchpref.isChecked()){
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("memoryplusreminder")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Log.d("", "unsub ok");
                                            }
                                            Log.d("", "unsub fail");
                                        }
                                    });
                        }
                        else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("memoryplusreminder")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Log.d("", "unsub ok");
                                            }
                                            Log.d("", "unsub fail");
                                        }
                                    });
                        }
                        return false;
                    }
                });
                return true;
            }

            //if change user password is clicked
            else if(key.equals("changeuserpassword")){
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                LinearLayout ll = new LinearLayout(getContext());

                final EditText oldpsw = new EditText(getContext());
                oldpsw.setInputType(InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                oldpsw.setHint("Şu anda kullandığınız şifre");
                checkFields(oldpsw);

                final EditText inputpsw = new EditText(getContext());
                inputpsw.setInputType(InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputpsw.setHint("Yeni şifre");
                checkFields(inputpsw);

                final EditText inputcpsw = new EditText(getContext());
                inputcpsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputcpsw.setHint("Yeni şifreyi onayla");
                checkFields(inputcpsw);

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
                                //update user's password
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
                                                        Toast.makeText(getContext(), "Hata! Şifre değiştirilemedi.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Şifreniz başarıyla değiştirildi.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getContext(), "Hatalı şifre", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Şifreler eşleşmiyor, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
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

            //if change admin pin is clicked
            else if(key.equals("changeadminpin")){
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                LinearLayout ll = new LinearLayout(getContext());

                final EditText inputpin = new EditText(getContext());
                inputpin.setInputType(InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputpin.setHint("Yeni yönetici şifresi");
                checkFields(inputpin);

                final EditText inputcpin = new EditText(getContext());
                inputcpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputcpin.setHint("Yeni şifreyi onayla");
                checkFields(inputcpin);

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

    //check empty inputs
    public static void checkFields(EditText field){
        if(field.length()==0)
        {
            field.requestFocus();
            field.setError("Bu alan boş bırakılamaz.");
        }
    }
}