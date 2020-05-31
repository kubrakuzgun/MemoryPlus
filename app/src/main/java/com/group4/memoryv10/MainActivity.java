package com.group4.memoryv10;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            displayUsageWarning();
        } else {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("error", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("NewToken", token);
                    }
                });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                Log.e("Token",mToken);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("memoryplusreminder")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d("", "subcription ok");
                        }
                        Log.d("", "subcription fail");
                    }
                });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
    }

    public void displayUsageWarning(){
        //create alert dialog to display usage warning
        android.app.AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        LinearLayout ll = new LinearLayout(MainActivity.this);
        alert.setTitle("Dikkat!");

        TextView warning1 = new TextView(MainActivity.this);
        warning1.setText("- Hafıza kutusu testinin 3 haftada 1 tekrarlanması gerekmektedir.");

        TextView warning1_1 = new TextView(MainActivity.this);
        warning1_1.setText("- Daha az ya da çok sıklıkta uygulanması kesinlikle tavsiye edilmemektedir");

        TextView warning2 = new TextView(MainActivity.this);
        warning2.setText("");

        TextView warning4 = new TextView(MainActivity.this);
        warning4.setText("");

        TextView warning5 = new TextView(MainActivity.this);
        warning5.setText("");
        TextView warning6 = new TextView(MainActivity.this);
        warning6.setText("");

        TextView warning3 = new TextView(MainActivity.this);
        warning3.setText("- Mini Oyunların tekrarlanma sıklığı hastanın durumuna göre hasta yakını tarafından belirlenmelidir." );

        TextView warning3_3 = new TextView(MainActivity.this);
        warning3_3.setText("- Gün içerisinde aynı oyunun birden fazla oynanması tavsiye edilmemektedir");

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(warning2);
        ll.addView(warning1);
        ll.addView(warning4);
        ll.addView(warning1_1);
        ll.addView(warning5);
        ll.addView(warning3);
        ll.addView(warning6);
        ll.addView(warning3_3);

        alert.setView(ll);
        alert.setPositiveButton("Anladım", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });
        alert.show();
    }
}
