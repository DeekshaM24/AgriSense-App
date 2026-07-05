package com.example.agrisense.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agrisense.R;
import com.example.agrisense.database.DatabaseHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PestDetectionActivity extends AppCompatActivity {

    private RadioGroup rgPestStatus;
    private MaterialCardView cardResult;
    private TextView txtTreatment;
    private Spinner spinnerCrop;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pest_detection);

        dbHelper = new DatabaseHelper(this);
        rgPestStatus = findViewById(R.id.rgPestStatus);
        cardResult = findViewById(R.id.cardResult);
        txtTreatment = findViewById(R.id.txtTreatment);
        spinnerCrop = findViewById(R.id.spinnerPestCrop);

        setupCropSpinner();

        findViewById(R.id.btnDetect).setOnClickListener(v -> detectPest());
    }

    private void setupCropSpinner() {
        Cursor cursor = dbHelper.getAllCrops();
        List<String> cropNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            cropNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_NAME)));
        }
        cursor.close();

        if (cropNames.isEmpty()) {
            Toast.makeText(this, "Add a crop first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_black, cropNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrop.setAdapter(adapter);
    }

    private void detectPest() {
        int selectedId = rgPestStatus.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Please select a condition", Toast.LENGTH_SHORT).show();
            return;
        }

        cardResult.setVisibility(View.VISIBLE);
        String cropName = spinnerCrop.getSelectedItem().toString();

        if (selectedId == R.id.rbHealthy) {
            txtTreatment.setText(cropName + " is healthy! Continue regular irrigation and organic fertilizer application.");
        } else if (selectedId == R.id.rbMinor) {
            txtTreatment.setText("Minor symptoms on " + cropName + ". Apply Neem Oil spray and remove affected leaves.");
        } else if (selectedId == R.id.rbSerious) {
            txtTreatment.setText("Serious infection on " + cropName + "! Immediate fungicide/pesticide required. Consult an expert.");
        }
    }
}