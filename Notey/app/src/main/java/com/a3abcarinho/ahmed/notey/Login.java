package com.a3abcarinho.ahmed.notey;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextInputLayout email, password;
    private Button loginButton;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (TextInputLayout) findViewById(R.id.login_email);
        password = (TextInputLayout) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login);

        firebaseAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Login.this.email.getEditText().getText().toString().trim();
                String password = Login.this.password.getEditText().getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    logIn(email, password);
                }

            }
        });

    }

    private void logIn(String email, String password){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in, please wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        //if Login successful

                        if (task.isSuccessful()) {

                            Intent mainIntent = new Intent(Login.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                            Toast.makeText(Login.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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