package com.a3abcarinho.ahmed.notey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private RecyclerView mNotesList;
    private GridLayoutManager gridLayoutManager;
    private DatabaseReference fNotesDatabase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting RecyclerView

        mNotesList = (RecyclerView) findViewById(R.id.notes_list);

        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mNotesList.setHasFixedSize(true);
        mNotesList.setLayoutManager(gridLayoutManager);

        fAuth = FirebaseAuth.getInstance();
        fNotesDatabase = FirebaseDatabase.getInstance().getReference();


        if (fAuth.getCurrentUser() != null) {
            fNotesDatabase.child("Notes").child(fAuth.getCurrentUser().getUid());
        }

        updateUI();

        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    //Load Notes Data

    private void loadData() {
        Query query = fNotesDatabase.orderByValue();
        FirebaseRecyclerAdapter<NoteModel, NoteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NoteModel, NoteViewHolder>(

                NoteModel.class,
                R.layout.single_note_layout,
                NoteViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final NoteViewHolder viewHolder, NoteModel model, int position) {
                final String noteId = getRef(position).getKey();

                fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("timestamp")) {
                            String title = dataSnapshot.child("title").getValue().toString();
                            String timestamp = dataSnapshot.child("timestamp").getValue().toString();
                            String content = dataSnapshot.child("content").getValue().toString();


                            viewHolder.setNoteTitle(title);
                            //viewHolder.setNoteTime(timestamp);
                            viewHolder.setNoteContent(content);

                            getTime getTimeAgo = new getTime();
                            viewHolder.setNoteTime(getTimeAgo.getTimeAgo(Long.parseLong(timestamp), getApplicationContext()));

                            viewHolder.card.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MainActivity.this, NewNote.class);
                                    intent.putExtra("noteId", noteId);
                                    startActivity(intent);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mNotesList.setAdapter(firebaseRecyclerAdapter);


    }

    private void updateUI(){

        if (fAuth.getCurrentUser() != null){
            Log.i("MainActivity", "fAuth != null");
        } else {
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();
            Log.i("MainActivity", "fAuth == null");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.add_note:
                Intent newIntent = new Intent(MainActivity.this, NewNote.class);
                startActivity(newIntent);
                break;
        }

        return true;
    }


    //Calculate time since the note was created


    public static class getTime {
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        public static String getTimeAgo(long time, Context context){

            if (time < 1000000000000L) {

                // If timestamp given in seconds, convert to millis
                time *= 1000;

            }

            long now = System.currentTimeMillis();

            if (time > now || time <= 0){

                return null;

            }

            final long diff = now - time;

            if (diff < MINUTE_MILLIS){
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS){
                return (diff / MINUTE_MILLIS + " minutes ago");
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS){
                return (diff / HOUR_MILLIS + " hours ago");
            } else if (diff < 48 * HOUR_MILLIS){
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }

        }
    }

}