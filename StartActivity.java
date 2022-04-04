package com.example.mylocationandactivityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference dbRef = db.getReference();
//        dbRef.child("message").setValue("IT works");
    }

    public void gotoMainActivity(View view) {
        EditText editText = findViewById(R.id.editTextTravelName);
        String dataTableName = editText.getText().toString()
                .trim().replaceAll(" +", "_").toLowerCase();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dataTableName", dataTableName);
        startActivity(intent);
    }
}