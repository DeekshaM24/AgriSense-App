package com.example.agrisense.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.agrisense.models.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvNotes;
    private List<Note> noteList = new ArrayList<>();
    private NoteAdapter adapter;

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
        setContentView(R.layout.activity_notes);

        dbHelper = new DatabaseHelper(this);
        rvNotes = findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAddNote);
        fab.setOnClickListener(v -> showAddNoteDialog());

        loadNotes();
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        EditText etTitle = view.findViewById(R.id.etNoteTitle);
        EditText etDesc = view.findViewById(R.id.etNoteDesc);

        builder.setView(view)
                .setTitle("Add Farm Note")
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

                    if (!title.isEmpty()) {
                        if (dbHelper.addNote(title, desc, date)) {
                            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
                            loadNotes();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadNotes() {
        noteList.clear();
        Cursor cursor = dbHelper.getAllNotes();
        if (cursor.moveToFirst()) {
            do {
                noteList.add(new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_DATE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter = new NoteAdapter(noteList);
        rvNotes.setAdapter(adapter);
    }

    class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
        List<Note> list;
        NoteAdapter(List<Note> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Note note = list.get(position);
            holder.text1.setText(note.getTitle());
            holder.text2.setText(note.getDate() + " - " + note.getDescription());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            ViewHolder(View v) {
                super(v);
                text1 = v.findViewById(android.R.id.text1);
                text2 = v.findViewById(android.R.id.text2);
            }
        }
    }
}