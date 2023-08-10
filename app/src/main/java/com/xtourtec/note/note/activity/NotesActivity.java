package com.xtourtec.note.note.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.xtourtec.note.R;
import com.xtourtec.note.note.rv.NotesViewModel;
import com.xtourtec.note.note.rv.adapter.NoteAdapter;

public class NotesActivity extends AppCompatActivity {
    private static final String TAG = "NotesActivity";

    private RecyclerView rvNotes;
    private NoteAdapter noteAdapter;
    private NotesViewModel viewModel;
    private EditText etKeyword;

    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        applyPermission();

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        etKeyword = findViewById(R.id.et_keyword);
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                listenKey();
            }
        });

        rvNotes = findViewById(R.id.rv_notes);
        noteAdapter = new NoteAdapter(this);
        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rvNotes.setAdapter(noteAdapter);
        viewModel.getNotes().observe(this, notes -> {
            noteAdapter.setNotes(notes);
            Log.i(TAG, "onCreate: " + notes);
        });
        findViewById(R.id.iv_edit).setOnClickListener(v -> startActivity(new Intent(NotesActivity.this, EditNoteActivity.class)));
    }


    private void applyPermission() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        final int REQUEST_CODE = 1;


        // 检查该权限是否已经获取

        for (String permission : permissions) {
            //  GRANTED---授权  DINIED---拒绝
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }
        }


            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }



    }

    private String lastKey = "";
    private Thread listenKeyThread;

    private void listenKey() {
        if (listenKeyThread != null && listenKeyThread.isAlive()) return;
        listenKeyThread = new Thread(() -> {
            String key;
            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                key = etKeyword.getText().toString().trim();
                if (!key.equals(lastKey)) {
                    noteAdapter.filter(key);
                    lastKey = key;
                }
            } while (inputMethodManager.isActive());
        });
        listenKeyThread.start();

    }

}