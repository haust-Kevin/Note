package com.xtourtec.note.note.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.xtourtec.note.note.db.entity.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long insert(Note note);

    @Query("delete from tb_note where id =:id")
    void deleteById(long id);

    @Update
    int update(Note note);

    @Query("select * from tb_note where title like '%'||:str||'%' or content like '%'||:str||'%'")
    LiveData<List<Note>> query(String str);

    @Query("select * from tb_note where id = :id")
    Note selectById(long id);

    @Query("select * from tb_note order by id desc")
    LiveData<List<Note>> selectAll();
}
