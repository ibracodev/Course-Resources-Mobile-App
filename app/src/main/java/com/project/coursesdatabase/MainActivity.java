package com.project.coursesdatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.text.*;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener {
    private FirebaseAuth mAuth;
    String TAG = "MainActivity";

    static FirebaseFirestore db;

    //FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    Button btn_upload,btn_download;
    EditText c_name_field;
    Spinner courseListSpinner;
    TableRow r;


    String course_name="default";// Name of course/Folder in Storage
    String username;
    String Description="No Description";

    Boolean newCourse = false;
    ArrayList<String>CourseNames = new ArrayList<String>();
    ArrayAdapter<CharSequence> adapter;
    FirebaseUser user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.lightgrey));

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        username=user.getEmail().toString();
        getSupportActionBar().setTitle("Welcome: " + username);
        db = FirebaseFirestore.getInstance();

        btn_upload = findViewById(R.id.btn_upload);
        btn_download=findViewById(R.id.btn_d);


        r=findViewById(R.id.course_row);
        c_name_field=findViewById(R.id.c_name_field);

        //Database
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(course_name);

        btn_upload.setOnClickListener(this);
        btn_download.setOnClickListener(this);


        courseListSpinner=findViewById(R.id.courses_list);
        CourseNames = new ArrayList<String>();
        courseListSpinner.setAdapter(null);


        Log.d(TAG, "on create" );


        r.setVisibility(View.INVISIBLE);
        Log.d(TAG, "done");



    }


    public void onResume(){
        Log.d("bla","Onresume called");
        super.onResume();
        CourseNames = new ArrayList<String>();
        courseListSpinner.setAdapter(null);
        settingSpinner();
    }
    public void onPause(){
        Log.d("bla","Onpause called");
        super.onPause();
        courseListSpinner.setAdapter(null);
    }


    public void settingSpinner(){
        //source https://stackoverflow.com/questions/52715924/how-i-can-get-list-of-name-of-folder-in-firebase-storage-android
        CourseNames = new ArrayList<String>();
        courseListSpinner.setAdapter(null);
        StorageReference listRef = FirebaseStorage.getInstance().getReference("/");


        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        Log.d("FirebaseList", listRef.getName());
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            Log.d("FirebaseList", prefix.getName());
                            // This will give you a folder name
                            // You may call listAll() recursively on them.
                            CourseNames.add(prefix.getName());
                            Log.d(TAG, "course name " + prefix.getName());
                            adapter.notifyDataSetChanged();
                        }
                        CourseNames.add("None of the above");
                        adapter.notifyDataSetChanged();
                        //for (StorageReference item : listResult.getItems()) {
                        // All the items under listRef.
                        //}
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, CourseNames);
        Log.d(TAG, "array adapter set");

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseListSpinner.setAdapter(adapter);
        courseListSpinner.setOnItemSelectedListener(this);
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
                login.putExtra("msg","Signing Out");
                Toast.makeText(this, "Logging Out",
                        Toast.LENGTH_SHORT).show();
                startActivity(login);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {

        //upload button
        if (view.getId()== R.id.btn_upload){
            int n=courseListSpinner.getAdapter().getCount();
            if(r.getVisibility()==View.VISIBLE && c_name_field.getText().toString().isEmpty() && courseListSpinner.getSelectedItemPosition()==n-1){
                Toast.makeText(MainActivity.this, "Course Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
            else if(r.getVisibility()==View.VISIBLE && !c_name_field.getText().toString().isEmpty() && courseListSpinner.getSelectedItemPosition()==n-1){
                course_name=c_name_field.getText().toString();
                Intent uploadIntent = new Intent(this, UploadFiles.class);
                uploadIntent.putExtra("CourseName", course_name);
                uploadIntent.putExtra("Username", username);
                startActivity(uploadIntent);
            }
            else {
                course_name = courseListSpinner.getSelectedItem().toString();
                Intent uploadIntent = new Intent(this, UploadFiles.class);
                uploadIntent.putExtra("CourseName", course_name);
                uploadIntent.putExtra("Username", username);
                startActivity(uploadIntent);
            }



        }
        if (view.getId()== R.id.btn_d){
            int n=courseListSpinner.getAdapter().getCount();
            if(r.getVisibility()==View.VISIBLE && c_name_field.getText().toString().isEmpty() && courseListSpinner.getSelectedItemPosition()==n-1){
                Toast.makeText(MainActivity.this, "Course Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
            else if(r.getVisibility()==View.VISIBLE && !c_name_field.getText().toString().isEmpty() && courseListSpinner.getSelectedItemPosition()==n-1)
            {
                course_name=c_name_field.getText().toString();
                Intent intent=new Intent(getApplicationContext(),ViewFiles.class);
                intent.putExtra("CourseName",course_name);
                startActivity(intent);
            }
            else {
                course_name = courseListSpinner.getSelectedItem().toString();
                Intent intent=new Intent(getApplicationContext(),ViewFiles.class);
                intent.putExtra("CourseName",course_name);
                startActivity(intent);
            }


        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int n=courseListSpinner.getAdapter().getCount();

        adapter.notifyDataSetChanged();
        if(courseListSpinner.getSelectedItemPosition()==n-1){
            r.setVisibility(View.VISIBLE);
            newCourse = true;
        }
        else{
            course_name = courseListSpinner.getSelectedItem().toString();
            r.setVisibility(View.INVISIBLE);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference(course_name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(v.getId()==R.id.c_name_field){
            course_name=c_name_field.getText().toString();
            Log.d("cname",course_name);
        }
        return false;
    }




    public void sendNotification(){

    }
}