package com.project.coursesdatabase;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener {

    static FirebaseFirestore db;

    //FirebaseStorage storage;
    StorageReference storageReference;
    //DatabaseReference databaseReference;

    Button btn_upload,btn_download;
    TextView name;
    EditText c_name_field;
    Spinner s;
    TableRow r;
    String course_name="default";// Name of course/Folder in Storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        getSupportActionBar().setTitle("Welcome: " + intent.getStringExtra("username"));
        db = FirebaseFirestore.getInstance();

        btn_upload = findViewById(R.id.btn_upload);
        btn_download=findViewById(R.id.btn_d);
        s=findViewById(R.id.courses_list);
        name = findViewById(R.id.filename);
        r=findViewById(R.id.course_row);
        c_name_field=findViewById(R.id.c_name_field);

        //Database
        storageReference = FirebaseStorage.getInstance().getReference();
        //databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");

        btn_upload.setOnClickListener(this);
        btn_download.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.courses_list,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        r.setVisibility(View.INVISIBLE);
        s.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()== R.id.btn_upload){


            int n=s.getAdapter().getCount();
            if(s.getSelectedItemPosition()==n-1){
                course_name=c_name_field.getText().toString();

            }
            else{
                course_name=s.getSelectedItem().toString();

            }
            selectFiles();

        }
        if (view.getId()== R.id.btn_d){
            StorageReference downloadReference=FirebaseStorage.getInstance().getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/course-resources-and-database.appspot.com/o/Artificial%20Intelligence%2FClassification_of_Breast_Cancer_Images_by_Transfer_Learning_Approach_Using_Different_Patching_Sizes.pdf?alt=media&token=d197e559-7012-4d9f-9db7-f635f9a43070");
            final long ONE_MEGABYTE = 1024 * 1024;
            downloadReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Toast
                            .makeText(MainActivity.this,
                                    "Download Suceeded!!",
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast
                            .makeText(MainActivity.this,
                                    "Download Failed : " +exception.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();

                    Log.d("error",exception.getMessage());
                }
            });

        //Code Adapted from https://firebase.google.com/docs/storage/android/download-files#:~:text=To%20download%20a%20file%2C%20first,an%20object%20in%20Cloud%20Storage.
        }
    }

    public void selectFiles(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file to upload"), 1);
    }

    public void UploadFiles(Uri data){
        Log.d("UPLOAD FILE", data.getPath());
        name.setText(getFileName(data));

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference ref = storageReference.child(course_name +"/"+ getFileName(data));

        ref.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            UploadFiles(data.getData());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int n=s.getAdapter().getCount();
        if(s.getSelectedItemPosition()==n-1){
            r.setVisibility(View.VISIBLE);

        }
        else{
            course_name=s.getSelectedItem().toString();
            r.setVisibility(View.INVISIBLE);
        }
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
}