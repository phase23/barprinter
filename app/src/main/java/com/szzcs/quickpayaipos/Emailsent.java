package com.szzcs.quickpayaipos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Emailsent extends AppCompatActivity {
    Button efinshtxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailsent);

        efinshtxt = (Button)findViewById(R.id.emailfinish);


        efinshtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Emailsent.this, MainActivity.class);

                startActivity(intent);

            }

        });


    }
}