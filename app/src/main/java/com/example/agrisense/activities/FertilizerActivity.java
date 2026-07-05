package com.example.agrisense.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrisense.R;
import com.example.agrisense.database.DatabaseHelper;
import com.example.agrisense.models.Fertilizer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FertilizerActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvFertilizer;
    private List<Fertilizer> fertilizerList = new ArrayList<>();
    private FertilizerAdapter adapter;

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
        setContentView(R.layout.activity_fertilizer);

        dbHelper = new DatabaseHelper(this);
        rvFertilizer = findViewById(R.id.rvFertilizer);
        rvFertilizer.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnAddFertilizer).setOnClickListener(v -> showAddFertilizerDialog());

        loadFertilizer();
    }

    private void showAddFertilizerDialog() {
        Cursor cropCursor = dbHelper.getAllCrops();
        if (cropCursor.getCount() == 0) {
            Toast.makeText(this, "Add a crop first", Toast.LENGTH_SHORT).show();
            cropCursor.close();
            return;
        }

        List<String> cropNames = new ArrayList<>();
        Map<String, Integer> cropMap = new HashMap<>();
        while (cropCursor.moveToNext()) {
            String name = cropCursor.getString(cropCursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_NAME));
            int id = cropCursor.getInt(cropCursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_ID));
            cropNames.add(name);
            cropMap.put(name, id);
        }
        cropCursor.close();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_fertilizer, null);
        Spinner spinner = view.findViewById(R.id.spinnerFertCrops);
        EditText etName = view.findViewById(R.id.etFertName);
        EditText etQty = view.findViewById(R.id.etFertQty);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_white, cropNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Log Fertilizer")
                .setPositiveButton("Save", (dialog, which) -> {
                    String selectedCrop = spinner.getSelectedItem().toString();
                    int cropId = cropMap.get(selectedCrop);
                    String fertName = etName.getText().toString().trim();
                    String fertQty = etQty.getText().toString().trim();
                    String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                    if (!fertName.isEmpty() && !fertQty.isEmpty()) {
                        if (dbHelper.addFertilizer(cropId, fertName, fertQty, date)) {
                            loadFertilizer();
                        }
                    } else {
                        Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadFertilizer() {
        fertilizerList.clear();
        Cursor cursor = dbHelper.getAllFertilizers();
        if (cursor.moveToFirst()) {
            do {
                fertilizerList.add(new Fertilizer(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FERT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FERT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FERT_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FERT_DATE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new FertilizerAdapter(fertilizerList);
        rvFertilizer.setAdapter(adapter);
    }

    class FertilizerAdapter extends RecyclerView.Adapter<FertilizerAdapter.ViewHolder> {
        List<Fertilizer> list;
        FertilizerAdapter(List<Fertilizer> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_aesthetic, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Fertilizer fert = list.get(position);
            holder.tvMain.setText(fert.getCropName() + " - " + fert.getName());
            holder.tvSub.setText("Qty: " + fert.getQuantity() + " | Date: " + fert.getDate());
            
            holder.ivDelete.setOnClickListener(v -> {
                if (dbHelper.deleteFertilizer(fert.getId())) {
                    Toast.makeText(FertilizerActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadFertilizer();
                }
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMain, tvSub;
            ImageView ivDelete;
            ViewHolder(View v) {
                super(v);
                tvMain = v.findViewById(R.id.tvMainText);
                tvSub = v.findViewById(R.id.tvSubText);
                ivDelete = v.findViewById(R.id.ivDelete);
            }
        }
    }
}