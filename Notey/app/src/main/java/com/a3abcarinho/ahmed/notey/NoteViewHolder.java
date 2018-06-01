package com.a3abcarinho.ahmed.notey;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    // Note View Holder

    View view;

    TextView title, content, texttime;
    CardView card;

    public NoteViewHolder(View itemView) {
        super(itemView);

        view = itemView;

        //Find Note Views

        title = view.findViewById(R.id.note_title);
        content = view.findViewById(R.id.note_content);
        texttime = view.findViewById(R.id.note_time);
        card = view.findViewById(R.id.note_card);

    }
    // /set Note Title, Content, Time

    public void setNoteTitle(String title) {
        this.title.setText(title);
    }

    public void setNoteContent(String content) {
        this.content.setText(content);
    }

    public void setNoteTime(String time) {
        texttime.setText(time);
    }

}
