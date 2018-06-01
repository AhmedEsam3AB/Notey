package com.a3abcarinho.ahmed.notey;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private TextInputLayout name, email, password;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = (Button) findViewById(R.id.register);
        name = (TextInputLayout) findViewById(R.id.name);
        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.password);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = RegisterActivity.this.name.getEditText().getText().toString().trim();
                String email = RegisterActivity.this.email.getEditText().getText().toString().trim();
                String password = RegisterActivity.this.password.getEditText().getText().toString().trim();

                registerUser(name, email, password);

            }
        });

    }

    private void registerUser(final String name, String email, String password){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing your request, please wait...");

        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //If register Successful

                        if (task.isSuccessful()){

                            databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                                    .child("basic").child("name").setValue(name)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                progressDialog.dismiss();

                                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                                Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_SHORT).show();

                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(RegisterActivity.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

}
