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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.text.*;

//Following reference was used for the parts of code below : https://www.youtube.com/watch?v=axChfqYiZwc


public class ViewFiles extends AppCompatActivity {
    ListView list;
    DatabaseReference dbref;
    ArrayList<FileClass> files;
    String cname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_files);
        list=findViewById(R.id.list_view);
        Intent intent = getIntent();
        cname=intent.getStringExtra("CourseName");
        getSupportActionBar().setTitle("Previouses for " + cname);
        files=new ArrayList<FileClass>();
        view_all_files();
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

       dbref= FirebaseDatabase.getInstance().getReference(cname);
       dbref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
              for(DataSnapshot d: snapshot.getChildren()){
                  FileClass f=d.getValue(FileClass.class);
                  files.add(f);
              }

               CustomArrayAdapter adapter = new  CustomArrayAdapter(getApplicationContext(), files);
               ListView listView = (ListView) findViewById(R.id.list_view);
               list.setAdapter(adapter);

//              String[] uploads=new String[files.size()];
//              for(int i=0;i<uploads.length;i++){
//                  uploads[i]=files.get(i).getName();
//               }
//              ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,uploads);
//              list.setAdapter(adapter);

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }


}