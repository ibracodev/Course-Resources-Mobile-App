package com.project.coursesdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.text.*;

//Following reference was used for the parts of code below : https://www.youtube.com/watch?v=axChfqYiZwc


public class ViewFiles extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ListView list;
    DatabaseReference dbref;
    ArrayList<FileClass> files;
    String cname;
    private FirebaseAuth mAuth;
    Spinner yearSpinner;
    TextView coursename;
    String year = "None"; //random value set as default
    CustomArrayAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_files);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.lightgrey));
        list=findViewById(R.id.course_List);
        coursename = findViewById(R.id.courseTitletxt);
        yearSpinner = findViewById(R.id.yearSpin);
        Intent intent = getIntent();
        cname=intent.getStringExtra("CourseName");
        getSupportActionBar().setTitle("Viewing Previouses for " + cname);

        files=new ArrayList<FileClass>();
        coursename.setText("Previouses For " + cname);

        ArrayAdapter<CharSequence> yearadapter = ArrayAdapter.createFromResource(this, R.array.years_list,
                android.R.layout.simple_spinner_item);
        yearadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearadapter);
        yearSpinner.setOnItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();


        view_all_files();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //Following reference was used for the parts of code below : https://www.youtube.com/watch?v=axChfqYiZwc
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileClass f=files.get(position);

                Intent intent=new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(f.getUrl()));
                startActivity(intent);
            }
        });

    }

    private void view_all_files(){
        //Following reference was used for the parts of code below : https://www.youtube.com/watch?v=axChfqYiZwc
        files=new ArrayList<FileClass>();
        dbref= FirebaseDatabase.getInstance().getReference(cname);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d: snapshot.getChildren()){
                    FileClass f=d.getValue(FileClass.class);
                    if (f.getYearuploaded().equals(yearSpinner.getSelectedItem().toString())){
                        files.add(f);
                    }

                }
                adapter = new CustomArrayAdapter(getApplicationContext(), files);
                //ListView listView = (ListView) findViewById(R.id.course_List);
                list.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        view_all_files();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }




}