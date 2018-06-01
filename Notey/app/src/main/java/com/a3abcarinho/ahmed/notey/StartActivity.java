package com.a3abcarinho.ahmed.notey;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
public class StartActivity extends AppCompatActivity {

    private Button register, login;

    //Firebase Authentication
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        register = (Button) findViewById(R.id.start_register);
        login = (Button) findViewById(R.id.start_login);
        firebaseAuth = FirebaseAuth.getInstance();

        //If get current User not null Start Activity intent

        updateUI();

        //Buttons ClickListeners

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    //Start Register or Login Activities

    private void register(){
        Intent registerIntent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void login(){
        Intent loginIntent = new Intent(StartActivity.this, Login.class);
        startActivity(loginIntent);
    }

    private void updateUI(){
        if (firebaseAuth.getCurrentUser() != null){
            Intent startIntent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
        }
    }

}