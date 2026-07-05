package com.example.agrisense.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.example.agrisense.models.Irrigation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IrrigationActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvIrrigation;
    private List<Irrigation> irrigationList = new ArrayList<>();
    private IrrigationAdapter adapter;

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
        setContentView(R.layout.activity_irrigation);

        dbHelper = new DatabaseHelper(this);
        rvIrrigation = findViewById(R.id.rvIrrigation);
        rvIrrigation.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnAddIrrigation).setOnClickListener(v -> showAddIrrigationDialog());

        loadIrrigation();
    }

    private void showAddIrrigationDialog() {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_crop_irrigation, null);
        Spinner spinner = view.findViewById(R.id.spinnerCrops);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_white, cropNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(view)
                .setTitle("Schedule Irrigation")
                .setPositiveButton("Add", (dialog, which) -> {
                    String selectedCrop = spinner.getSelectedItem().toString();
                    int cropId = cropMap.get(selectedCrop);
                    String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    if (dbHelper.addIrrigation(cropId, date, "Upcoming")) {
                        loadIrrigation();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadIrrigation() {
        irrigationList.clear();
        Cursor cursor = dbHelper.getAllIrrigation();
        if (cursor.moveToFirst()) {
            do {
                irrigationList.add(new Irrigation(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IRR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CROP_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IRR_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IRR_STATUS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new IrrigationAdapter(irrigationList);
        rvIrrigation.setAdapter(adapter);
    }

    class IrrigationAdapter extends RecyclerView.Adapter<IrrigationAdapter.ViewHolder> {
        List<Irrigation> list;
        IrrigationAdapter(List<Irrigation> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_aesthetic, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Irrigation irr = list.get(position);
            holder.tvMain.setText(irr.getCropName());
            holder.tvSub.setText("Date: " + irr.getDate() + " | Status: " + irr.getStatus());
            
            holder.ivDelete.setOnClickListener(v -> {
                if (dbHelper.deleteIrrigation(irr.getId())) {
                    Toast.makeText(IrrigationActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadIrrigation();
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