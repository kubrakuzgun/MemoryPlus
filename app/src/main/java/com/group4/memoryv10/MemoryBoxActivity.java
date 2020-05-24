package com.group4.memoryv10;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MemoryBoxActivity extends AppCompatActivity {
    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorybox);

        //create action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Hafıza Kutusu");
        }

        startBtn = findViewById(R.id.startBtn);

        //when save button is clicked
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect to memory box test activity
                Intent intToMBTest = new Intent(MemoryBoxActivity.this, MB_TestActivity_Part1.class);
                startActivity(intToMBTest);
            }
        });
    }

    //when action bar back button clicked
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
