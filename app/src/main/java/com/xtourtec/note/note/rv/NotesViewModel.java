package com.xtourtec.note.note.rv;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.xtourtec.note.note.db.AppDatabase;
import com.xtourtec.note.note.db.dao.NoteDao;
import com.xtourtec.note.note.db.entity.Note;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private NoteDao noteDao;
    private LiveData<List<Note>> notes;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        noteDao = db.getNoteDao();
        notes = noteDao.selectAll();
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

}
