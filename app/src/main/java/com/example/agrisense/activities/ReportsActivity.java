package com.example.agrisense.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agrisense.R;
import com.example.agrisense.database.DatabaseHelper;

import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved language
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        TextView tvCrops = findViewById(R.id.tvTotalCrops);
        TextView tvIrrigation = findViewById(R.id.tvTotalIrrigation);
        TextView tvFertilizer = findViewById(R.id.tvTotalFertilizer);
        TextView tvNotes = findViewById(R.id.tvTotalNotes);

        tvCrops.setText(String.valueOf(dbHelper.getCount(DatabaseHelper.TABLE_CROPS)));
        tvIrrigation.setText(String.valueOf(dbHelper.getCount(DatabaseHelper.TABLE_IRRIGATION)));
        tvFertilizer.setText(String.valueOf(dbHelper.getCount(DatabaseHelper.TABLE_FERTILIZER)));
        tvNotes.setText(String.valueOf(dbHelper.getCount(DatabaseHelper.TABLE_NOTES)));

        TextView tvExportPath = findViewById(R.id.tvExportPath);
        findViewById(R.id.btnExport).setOnClickListener(v -> {
            tvExportPath.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.csv_generated), Toast.LENGTH_SHORT).show();
        });
    }
}