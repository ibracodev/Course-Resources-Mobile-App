package com.project.coursesdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

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


public class ViewFiles extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {
    ListView list;
    DatabaseReference dbref;
    ArrayList<FileClass> files;
    String cname;

    Spinner yearSpinner;
    TextView coursename;
    String year = "2020"; //random value set as default
    CustomArrayAdapter adapter;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_files);

        list=findViewById(R.id.course_List);
        coursename = findViewById(R.id.courseTitletxt);
        yearSpinner = findViewById(R.id.yearSpin);
        search = findViewById(R.id.searchView);

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

        view_all_files();

        search.setOnQueryTextListener(this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
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

        dbref= FirebaseDatabase.getInstance().getReference(cname);//+"/"+year);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d: snapshot.getChildren()){
                    FileClass f=d.getValue(FileClass.class);
                    files.add(f);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        year = yearSpinner.getSelectedItem().toString();
        view_all_files();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {


        return false;
    }
    //https://stackoverflow.com/questions/21827646/how-to-implement-search-in-custom-listview-in-android
}