package com.project.coursesdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button btn_login, btn_signup;
    EditText email, password;

    String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setTitle("Login or Sign Up");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
        password = findViewById(R.id.password);
        email = findViewById(R.id.username);

        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);

        mAuth.addAuthStateListener(authStateListener);
    }


    //https://stackoverflow.com/questions/50885891/one-time-login-in-app-firebaseauth
    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.getEmail() != null) {
            Log.d("Firebase User", "user is not null");
            Log.d("Firebase User", firebaseUser.getIdToken(true) +"");
            //Intent intent = new Intent(Login.this, MainActivity.class);
            //startActivity(intent);
            //finish();
        }
    };
    //it is returning a user logged in because firebase uses idtokens and not actual users
    //current log is showing the idtoken com.google.android.gms.tasks.zzw@379aadb logged in but we only want the autherized users to login
    // refer to this: https://stackoverflow.com/questions/54338834/android-studio-i-am-always-logged-in-although-there-are-no-users-in-my-firebas

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_signup){
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
        if (view.getId() == R.id.btn_login){
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
        }
    }
}