package com.project.coursesdatabase;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// Parts of the Code Adapted from https://www.youtube.com/watch?v=lmJHtSChZG0
public class UploadFiles extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Spinner years;
    Button upload;
    EditText description;
    private FirebaseAuth mAuth;
    //FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String course_name = "default";// Name of course/Folder in Storage
    String username = "";
    String desc = "No Description";
    FirebaseUser user;
    String topic = "notification";
    String TAG = "UploadFiles";
    int id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.lightgrey));

        years = findViewById(R.id.year_spinner);
        upload = findViewById(R.id.uploadButton);
        description = findViewById(R.id.prevDescription);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.years_list,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        years.setAdapter(adapter);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(course_name);


        user = FirebaseAuth.getInstance().getCurrentUser();
        username = user.getEmail().toString();
        Intent intent = getIntent();
        getSupportActionBar().setTitle("Welcome: " + username);

        course_name = intent.getStringExtra("CourseName");
        databaseReference = FirebaseDatabase.getInstance().getReference(course_name);
        mAuth = FirebaseAuth.getInstance();
        upload.setOnClickListener(this);
        years.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.uploadButton) {
            if (description.getText().toString().isEmpty()) {
                Toast.makeText(UploadFiles.this, "Course Description cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                selectFiles();
            }
        }
    }

    public void selectFiles() {
        // Parts of this section adapted from https://www.youtube.com/watch?v=lmJHtSChZG0
        //opens the file manager on your phone to select and upload files
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file to upload"), 1);
    }

    //when files are selected the upload function is called
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Parts of this section adapted from https://www.youtube.com/watch?v=lmJHtSChZG0
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            UploadFiles(data.getData());
        }
    }

    //upload function
    @SuppressLint("NotConstructor")
    public void UploadFiles(Uri data) {
        // Parts of this section adapted from https://www.youtube.com/watch?v=lmJHtSChZG0
        Log.d("UPLOAD FILE", data.getPath());

        //displays progress of the upload to the user
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        //placing in the storage
        StorageReference ref = storageReference.child(course_name + "/" + years.getSelectedItem().toString() + "/" + getFileName(data));
        //uploading to firebase
        ref.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();

                FileClass fclass = new FileClass(getFileName(data), url.toString());
                fclass.setUsername("Uploaded by " + username);
                fclass.setUploadtime(getTimeandDate());

                if (!description.getText().toString().isEmpty()) {
                    desc = description.getText().toString();
                }

                fclass.setDescc(desc);
                fclass.setYearuploaded(years.getSelectedItem().toString());
                databaseReference.child(databaseReference.push().getKey()).setValue(fclass);
                progressDialog.dismiss();

                //for notifications
                // The topic name can be optionally prefixed with "/topics/".
                // See documentation on defining a message payload.
                RemoteMessage message = new RemoteMessage.Builder(topic).addData("Course",course_name).build();
                // Send a message to the devices subscribed to the provided topic.

                FirebaseMessaging.getInstance().send(
                        new RemoteMessage.Builder( username+ "@fcm.googleapis.com")
                                .setMessageId(id+"")
                                .addData("CourseName", course_name)
                                .build());
                id++;
                // Response is a message ID string.
                Log.d(TAG, "message: " + message.getData() +" sent");
                //System.out.println("Successfully sent message: " + message);// + response);

                Toast.makeText(UploadFiles.this, fclass.getName() + " Uploaded!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(UploadFiles.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                + (int) progress + "%");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenu:
                mAuth.signOut();
                Intent login = new Intent(this, Login.class);
                Toast.makeText(this, "Logging Out",
                        Toast.LENGTH_SHORT).show();
                startActivity(login);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    String getTimeandDate() {
        // adapted from https://stackoverflow.com/questions/1305350/how-to-get-the-current-date-and-time-of-your-timezone-in-java
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+4:00"));
        String d = "Uploaded on " + df.format(date) + " GST";
        return d;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}