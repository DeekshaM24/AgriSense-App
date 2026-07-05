package com.example.agrisense.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agrisense.R;
import com.example.agrisense.database.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        
        TextView tvName = findViewById(R.id.tvProfileName);
        TextView tvEmail = findViewById(R.id.tvProfileEmail);
        TextView tvPhone = findViewById(R.id.tvProfilePhone);
        
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USERS + " LIMIT 1", null);
        if (cursor.moveToFirst()) {
            tvName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)));
            tvEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL)));
            tvPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PHONE)));
        }
        cursor.close();

        findViewById(R.id.btnProfileLogout).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}