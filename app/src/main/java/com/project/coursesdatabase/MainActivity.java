package com.project.coursesdatabase;
// Parts of the Code Adapted from https://www.youtube.com/watch?v=lmJHtSChZG0
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.*;
import java.text.*;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener {

    String TAG = "MainActivity";

    static FirebaseFirestore db;

    //FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    Button btn_upload,btn_download;
    EditText c_name_field,descc;
    Spinner courseListSpinner;
    TableRow r;


    String course_name="default";// Name of course/Folder in Storage
    String username;
    String Description="No Description";

    Boolean newCourse = false;
    ArrayList<String>CourseNames = new ArrayList<String>();
    ArrayAdapter<CharSequence> adapter;

    //logging out
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        username=intent.getStringExtra("username");
        getSupportActionBar().setTitle("Welcome: " + username);
        db = FirebaseFirestore.getInstance();

        btn_upload = findViewById(R.id.btn_upload);
        btn_download=findViewById(R.id.btn_d);
        courseListSpinner=findViewById(R.id.courses_list);

        r=findViewById(R.id.course_row);
        c_name_field=findViewById(R.id.c_name_field);
        descc=findViewById(R.id.desc);
        //Database
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(course_name);

        btn_upload.setOnClickListener(this);
        btn_download.setOnClickListener(this);


//        CourseNames.add("Operating Systems");
//        CourseNames.add("Introduction to Macroeconomics");
//        CourseNames.add("None of the Above");

        Log.d(TAG, "on create" );
        StorageReference listRef = FirebaseStorage.getInstance().getReference("/");

        //source https://stackoverflow.com/questions/52715924/how-i-can-get-list-of-name-of-folder-in-firebase-storage-android
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
                        // Uh-oh, an error occurred!
                    }
                });


        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, CourseNames);
        Log.d(TAG, "array adapter set");
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.courses_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseListSpinner.setAdapter(adapter);
        r.setVisibility(View.INVISIBLE);
        courseListSpinner.setOnItemSelectedListener(this);
        Log.d(TAG, "done");
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
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);
                Toast.makeText(this, "Profile",
                        Toast.LENGTH_SHORT).show();
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

            if(courseListSpinner.getSelectedItemPosition()==n-1){
                course_name=c_name_field.getText().toString();
            }
            else {
                course_name = courseListSpinner.getSelectedItem().toString();
            }

            databaseReference = FirebaseDatabase.getInstance().getReference(course_name);
            selectFiles();
        }
        if (view.getId()== R.id.btn_d){

            Intent intent=new Intent(getApplicationContext(),ViewFiles.class);
            intent.putExtra("CourseName",course_name);
            startActivity(intent);

        }
    }

    public void selectFiles(){
        //opens the file manager on your phone to select and upload files
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file to upload"), 1);
    }

    //when files are selected the upload function is called
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            UploadFiles(data.getData());
        }
    }

    //upload function
    public void UploadFiles(Uri data){
        Log.d("UPLOAD FILE", data.getPath());

        //displays progress of the upload to the user
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        //placing in the storage
        StorageReference ref = storageReference.child(course_name +"/"+ getFileName(data));
        //uploading to firebase
        ref.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri= taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url=uri.getResult();

                FileClass fclass= new FileClass(getFileName(data),url.toString());
                fclass.setUsername("Uploaded by "+username);
                fclass.setUploadtime(getTimeandDate());

                if(!descc.getText().toString().isEmpty()){
                    Description=descc.getText().toString();
                }

                fclass.setDescc(Description);
                databaseReference.child(databaseReference.push().getKey()).setValue(fclass);
                progressDialog.dismiss();

                if (newCourse == true) {
                    int n=courseListSpinner.getAdapter().getCount();
                    CourseNames.remove(n - 1);
                    CourseNames.add(c_name_field.getText().toString());
                    CourseNames.add("None of the Above");
                    adapter.notifyDataSetChanged();
                    //courseListSpinner.setAdapter(adapter);
                    newCourse =false;
                }


                Toast.makeText(MainActivity.this, fclass.getName() + " Uploaded!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            // Progress Listener for loading
            // percentage on the dialog box
            @Override
            public void onProgress(
                    UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage(
                        "Uploaded "
                                + (int)progress + "%");
            }

        });

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


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    } //from https://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content

    String getTimeandDate(){
        // adapted from https://stackoverflow.com/questions/1305350/how-to-get-the-current-date-and-time-of-your-timezone-in-java
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+4:00"));
        String d="Uploaded on "+df.format(date)+" GST";
        return d;
    }


    public void sendNotificatio(){

    }
}