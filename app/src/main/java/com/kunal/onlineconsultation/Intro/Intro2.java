package com.kunal.onlineconsultation.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.kunal.onlineconsultation.LoginActivity;
import com.kunal.onlineconsultation.R;

public class Intro2 extends AppCompatActivity {
LinearLayout next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro2);
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intro2.this, LoginActivity.class);
                intent.putExtra("location",getIntent().getExtras().get("location").toString());
                startActivity(intent);
            }
        });
    }
}
