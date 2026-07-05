package com.example.agrisense.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agrisense.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved language or default to English
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        setLocaleNoRecreate(lang);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        MaterialButton btnLang = findViewById(R.id.btnLang);
        btnLang.setOnClickListener(v -> showLanguageMenu());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                showSettingsMenu();
                return true;
            }
            return false;
        });

        setupButtons();
    }

    private void setupButtons() {
        findViewById(R.id.btnCrops).setOnClickListener(v -> startActivity(new Intent(this, CropsActivity.class)));
        findViewById(R.id.btnIrrigation).setOnClickListener(v -> startActivity(new Intent(this, IrrigationActivity.class)));
        findViewById(R.id.btnFertilizer).setOnClickListener(v -> startActivity(new Intent(this, FertilizerActivity.class)));
        findViewById(R.id.btnPests).setOnClickListener(v -> startActivity(new Intent(this, PestDetectionActivity.class)));
        findViewById(R.id.btnNotes).setOnClickListener(v -> startActivity(new Intent(this, NotesActivity.class)));
        findViewById(R.id.btnReports).setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
    }

    private void showLanguageMenu() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.btnLang));
        popup.getMenu().add(0, 1, 0, "English");
        popup.getMenu().add(0, 2, 0, "ಕನ್ನಡ (Kannada)");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) setLocale("en");
            else setLocale("kn");
            return true;
        });
        popup.show();
    }

    private void setLocale(String langCode) {
        setLocaleNoRecreate(langCode);
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", langCode);
        editor.apply();
        
        // Restart activity to apply language change to Dashboard and beyond
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void setLocaleNoRecreate(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void showSettingsMenu() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.bottomNavigation));
        popup.getMenu().add(0, 1, 0, getString(R.string.logout));
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            return true;
        });
        popup.show();
    }
}