package com.xtourtec.note.note.rv.adapter;


import static com.xtourtec.note.note.activity.EditNoteActivity.INTENT_NOTE_ID_KEY;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.xtourtec.note.R;
import com.xtourtec.note.note.activity.EditNoteActivity;
import com.xtourtec.note.note.db.AppDatabase;
import com.xtourtec.note.note.db.dao.NoteDao;
import com.xtourtec.note.note.db.entity.Note;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private static final String TAG = "NoteAdapter";

    static class NoteViewHolder extends RecyclerView.ViewHolder {


        TextView tvTitle;
        TextView tvTime;
        ImageView ivImg;

        CardView cardView;
        Note note;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvTime = itemView.findViewById(R.id.tv_note_time);
            ivImg = itemView.findViewById(R.id.iv_note_img);
            cardView = itemView.findViewById(R.id.card_note);
        }

        private void initNode(Context context, Note note) {
            Log.i(TAG, "initNode: ");
            this.note = note;
            tvTitle.setText(note.title);
            tvTime.setText(DateFormat.format(context.getString(R.string.time_format_list), note.time));
            if (note.imgPath != null && !"".equals(note.imgPath)) {
                Log.i(TAG, "initNode: " + note.imgPath);
                try {
                    String s = URLDecoder.decode(note.imgPath, "UTF-8");
                    Bitmap bm = BitmapFactory.decodeFile(s);
                    ivImg.setImageBitmap(bm);
                    ivImg.setVisibility(View.VISIBLE);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

            } else {
                ivImg.setVisibility(View.GONE);
            }
//            cardView.setBackgroundColor(note.color);
            cardView.findViewById(R.id.card_layout).setBackgroundColor(note.color);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(INTENT_NOTE_ID_KEY, note.id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

            itemView.setOnLongClickListener(v -> {
                AlertDialog dialog = new AlertDialog.Builder(context)

                        .setTitle(note.title)//设置对话框的标题
                        .setMessage("你要删除这个笔记吗？")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss())
                        .setPositiveButton("确定", (dialog12, which) -> {

//                                Toast.makeText(context, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                            AppDatabase database = AppDatabase.getDatabase(context);
                            NoteDao noteDao = database.getNoteDao();
                            AppDatabase.databaseWriteExecutor.execute(() -> {
                                noteDao.deleteById(note.id);
                                new Handler(context.getMainLooper()).post(() -> {
                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                });
                            });
                            dialog12.dismiss();
                        }).create();
                dialog.show();

                return true;
            });
        }

    }

    private List<Note> notes;
    private List<Note> query;

    private Context context;

    public NoteAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.initNode(context, query.get(position));

    }

    @Override
    public int getItemCount() {
        return query == null ? 0 : query.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        query = new ArrayList<>();
        query.addAll(notes);
        notifyDataSetChanged();
    }

    public void filter(String keyword) {
        if (notes == null || keyword == null) return;
        if (keyword.equals("")) {
            query.clear();
            query.addAll(notes);
        } else {
            query.clear();
            for (Note n : notes) {
                if (n.title.contains(keyword) || n.content.contains(keyword)) {
                    query.add(n);
                }
            }
            new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
        }
    }
}
