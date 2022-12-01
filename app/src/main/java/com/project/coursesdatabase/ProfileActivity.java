package com.project.coursesdatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private TextView profileName, major, prevUpload, standing;
    private ImageView profilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = findViewById(R.id.profileName);
        major = findViewById(R.id.profileName);
        prevUpload = findViewById(R.id.profileName);
        standing = findViewById(R.id.profileName);
        profilePicture = findViewById(R.id.imageView);

        //add a button to change profile picture or edit info???

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logoutMenu:
                mAuth.signOut();
                Intent login = new Intent(this, Login.class);
                startActivity(login);
                Toast.makeText(this, "Logging Out",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.profileMenu:
                Toast.makeText(this, "You Are Already Here",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}