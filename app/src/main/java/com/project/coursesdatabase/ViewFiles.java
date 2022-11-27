package com.project.coursesdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.text.*;

//Following reference was used for the parts of code below : https://www.youtube.com/watch?v=axChfqYiZwc


public class ViewFiles extends AppCompatActivity {

    String TAG = "ViewFiles";

    TextView courseTitle;

    DatabaseReference dbref;
    //ArrayList<FileClass> files = new ArrayList<FileClass>();
    ArrayList<String> fileNames = new ArrayList<String>();
    String cname;
    private ListView filesListView;

    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG,"inside onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_files);

        filesListView=findViewById(R.id.course_List);
        courseTitle = findViewById(R.id.courseTitletxt);//filesListView =

        Intent intent = getIntent();
        cname=intent.getStringExtra("CourseName");
        getSupportActionBar().setTitle("Previouses for " + cname);
        courseTitle.setText(cname);

        view_all_files();
        Log.d(TAG,"view all files done");

        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "OnItemClick", Toast.LENGTH_SHORT).show();
//                FileClass f=files.get(position);
//
//                Intent intent=new Intent();
//                intent.setType(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(f.getUrl()));
//                startActivity(intent);
            }
        });

    }


    private void view_all_files(){

        //source https://stackoverflow.com/questions/52715924/how-i-can-get-list-of-name-of-folder-in-firebase-storage-android
        StorageReference listRef = FirebaseStorage.getInstance().getReference(cname+"/");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        Log.d("FirebaseList", listRef.getName());
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            Log.d("FirebaseList", prefix.getName());
                            // This will give you a folder name
                            // You may call listAll() recursively on them
                        }

                        for (StorageReference item : listResult.getItems()) {
                        // All the items under listRef.
                            Log.d(TAG,"in add success "+ item.getName());
                            fileNames.add(item.getName());
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });

        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (String item : fileNames) {
            Log.d(TAG,"in forloop " + item);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", item);
            //map.put("title", item.getTitle());
            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.listitem;
        String[] from = {"name"};
        int[] to = {R.id.doctitle};//, R.id.titleTextView};

        // create and set the adapter
        adapter = new SimpleAdapter(this, data, resource, from, to);
        filesListView.setAdapter(adapter);
        Log.d(TAG,"done w/ adapter");
//       dbref= FirebaseDatabase.getInstance().getReference(cname);
//       dbref.addValueEventListener(new ValueEventListener() {
//           @Override
//           public void onDataChange(@NonNull DataSnapshot snapshot) {
//              for(DataSnapshot d: snapshot.getChildren()){
//                  FileClass f=d.getValue(FileClass.class);
//                  files.add(f);
//              }
//
//               CustomArrayAdapter adapter = new  CustomArrayAdapter(getApplicationContext(), files);
//               ListView listView = (ListView) findViewById(R.id.course_List);
//               list.setAdapter(adapter);
//
//              String[] uploads=new String[files.size()];
//              for(int i=0;i<uploads.length;i++){
//                  uploads[i]=files.get(i).getName();
//               }
//              ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,uploads);
//              list.setAdapter(adapter);
//
//           }
//
//           @Override
//           public void onCancelled(@NonNull DatabaseError error) {
//
//           }
//       });

    }


}