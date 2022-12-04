package com.project.coursesdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button btn_login, btn_signup;
    EditText email, password;
    String msg="";
    String TAG = "LoginActivity";
    ImageView ImageView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setTitle("Login or Sign Up");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.lightgrey));

        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        password = findViewById(R.id.password);
        email = findViewById(R.id.username);
        ImageView = (ImageView)findViewById(R.id.imageView2);

        ImageView.setImageResource(R.drawable.logoapp);
        //Logo generated using https://www.shopify.com/tools/logo-maker/onboarding/pick-space


        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
          //  https://stackoverflow.com/questions/56163255/stay-logged-in-with-firebase-to-the-app-when-closed
        }

    }


    public void onResume(){

        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            //  https://stackoverflow.com/questions/56163255/stay-logged-in-with-firebase-to-the-app-when-closed
        }

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_signup){
            if(email.getText().toString().isEmpty() || password.getText().toString().trim().isEmpty()){
                Toast.makeText(Login.this, "Email or Username cannot be empty!!", Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(Login.this, "Created User : " + email.getText(), Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
        }
        if (view.getId() == R.id.btn_login){
            if(email.getText().toString().isEmpty() || password.getText().toString().trim().isEmpty()){
                Toast.makeText(Login.this, "Email or Username cannot be empty!!", Toast.LENGTH_SHORT).show();
            }
            else {
            mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("username", user.getEmail());
                                startActivity(intent);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Authentication Failed",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        } }
    }
}