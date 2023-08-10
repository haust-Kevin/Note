package com.xtourtec.note.note.db.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_note")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public String title;

    public String content;

    public String imgPath;

    public long time;

    public int color;


    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", time=" + time +
                ", color=" + color +
                '}';
    }
}
