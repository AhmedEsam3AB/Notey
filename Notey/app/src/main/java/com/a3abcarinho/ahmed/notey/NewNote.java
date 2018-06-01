package com.a3abcarinho.ahmed.notey;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewNote extends AppCompatActivity {

    private Button create;
    private EditText title, content;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String noteID;

    private boolean isExist;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        try {
            noteID = getIntent().getStringExtra("noteId");

            if (!noteID.trim().equals("")) {
                isExist = true;
            } else {
                isExist = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        create = (Button) findViewById(R.id.new_note);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Notes").child(firebaseAuth.getCurrentUser().getUid());

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = NewNote.this.title.getText().toString().trim();
                String content = NewNote.this.content.getText().toString().trim();
                String noTitle = " ";
                String noContent = " ";

                //Check Note input states to add

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    createNote(title, content);
                }else if (!TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                    createNote(title, noContent);

                }else if (TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    createNote(noTitle, content);
                }else{
                    Intent intent = new Intent(NewNote.this,MainActivity.class);
                    startActivity(intent);
                }
                if(!isNetworkAvailable()){
                    Intent intent = new Intent(NewNote.this,MainActivity.class);
                    startActivity(intent);
                    }



            }
        });

        putData();
    }

    //Check if device is Connected

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void putData() {

        if (isExist) {
            databaseReference.child(noteID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();

                        NewNote.this.title.setText(title);
                        NewNote.this.content.setText(content);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void createNote(String title, String content) {

        if (firebaseAuth.getCurrentUser() != null) {

            if (isExist) {
                // UPDATE A NOTE
                Map updateMap = new HashMap();
                updateMap.put("title", this.title.getText().toString().trim());
                updateMap.put("content", this.content.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);

                databaseReference.child(noteID).updateChildren(updateMap);

                Intent intent = new Intent(NewNote.this,MainActivity.class);
                startActivity(intent);
            } else {
                // CREATE A NEW NOTE
                final DatabaseReference newNoteRef = databaseReference.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("content", content);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(NewNote.this,MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(NewNote.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
                mainThread.start();
            }



        } else {
            Toast.makeText(this, "USERS IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete_note:
                if (isExist) {
                    deleteNote();
                    Intent intent = new Intent(NewNote.this,MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(NewNote.this,MainActivity.class);
                    startActivity(intent);                }
                break;
        }

        return true;
    }

    //Delete Note Method

    private void deleteNote() {

        databaseReference.child(noteID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(NewNote.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    noteID = "no";
                    finish();
                } else {
                    Toast.makeText(NewNote.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}