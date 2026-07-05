package com.example.agrisense.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrisense.R;
import com.example.agrisense.database.DatabaseHelper;
import com.example.agrisense.models.Crop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CropsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvCrops;
    private List<Crop> cropList = new ArrayList<>();
    private CropAdapter adapter;

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
        setContentView(R.layout.activity_crops);

        dbHelper = new DatabaseHelper(this);
        rvCrops = findViewById(R.id.rvCrops);
        rvCrops.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAddCrop);
        fab.setOnClickListener(v -> showAddCropDialog());

        loadCrops();
    }

    private void loadCrops() {
        cropList.clear();
        Cursor cursor = dbHelper.getAllCrops();
        if (cursor.moveToFirst()) {
            do {
                cropList.add(new Crop(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_VARIETY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_PLANT_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_HARVEST_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_AREA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_NOTES))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        adapter = new CropAdapter(cropList);
        rvCrops.setAdapter(adapter);
    }

    private void showAddCropDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_crop, null);
        
        EditText etName = view.findViewById(R.id.etCropName);
        EditText etVariety = view.findViewById(R.id.etVariety);
        EditText etPlant = view.findViewById(R.id.etPlantDate);
        EditText etHarvest = view.findViewById(R.id.etHarvestDate);
        EditText etArea = view.findViewById(R.id.etArea);
        EditText etNotes = view.findViewById(R.id.etNotes);
        Button btnSuggest = view.findViewById(R.id.btnSuggestHarvest);

        btnSuggest.setOnClickListener(v -> {
            String cropName = etName.getText().toString().toLowerCase().trim();
            String plantDateStr = etPlant.getText().toString().trim();

            if (cropName.isEmpty() || plantDateStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_crop_plant_first), Toast.LENGTH_SHORT).show();
                return;
            }

            int days = 90; // Default
            Map<String, Integer> durations = new HashMap<>();
            durations.put("rice", 120);
            durations.put("paddy", 120);
            durations.put("bhata", 120);
            durations.put("wheat", 110);
            durations.put("tomato", 80);
            durations.put("maize", 100);
            durations.put("cotton", 160);
            durations.put("onion", 110);
            durations.put("potato", 100);

            for (String key : durations.keySet()) {
                if (cropName.contains(key)) {
                    days = durations.get(key);
                    break;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date plantDate = sdf.parse(plantDateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(plantDate);
                cal.add(Calendar.DAY_OF_YEAR, days);
                etHarvest.setText(sdf.format(cal.getTime()));
                Toast.makeText(this, getString(R.string.suggested_days, days), Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                Toast.makeText(this, getString(R.string.date_format_error), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(view)
                .setTitle("Add New Crop")
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String variety = etVariety.getText().toString();
                    String plant = etPlant.getText().toString();
                    String harvest = etHarvest.getText().toString();
                    String area = etArea.getText().toString();
                    String notes = etNotes.getText().toString();

                    if (!name.isEmpty()) {
                        dbHelper.addCrop(name, variety, plant, harvest, area, notes);
                        loadCrops();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    class CropAdapter extends RecyclerView.Adapter<CropAdapter.ViewHolder> {
        List<Crop> list;
        CropAdapter(List<Crop> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crop, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Crop crop = list.get(position);
            holder.tvName.setText(crop.getName());
            holder.tvVariety.setText(crop.getVariety());
            holder.tvDates.setText(crop.getPlantDate() + " - " + crop.getHarvestDate());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvVariety, tvDates;
            ViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tvItemCropName);
                tvVariety = v.findViewById(R.id.tvItemVariety);
                tvDates = v.findViewById(R.id.tvItemDates);
            }
        }
    }
}