package com.example.bookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editText;

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editUserName);
        btn = (Button) findViewById(R.id.btnEnter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
//              Toast.makeText(getApplicationContext(),text, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),ImageActivity.class);
                i.putExtra("text",text);
                startActivity(i);
            }
        });
    }
}