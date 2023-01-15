package vungnv.com.foodappmerchant.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.CategoryModel;
import vungnv.com.foodappmerchant.utils.ImagePicker;

public class AddCategoryActivity extends AppCompatActivity implements Constant {
    private Button btnOpenGallery, btnSave, btnCancel;
    private ImageView imgCate;
    private EditText edNameCate;

    private String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        init();

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.pickImage(AddCategoryActivity.this, new ImagePicker.OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri uri) {
                        imgCate.setImageURI(uri);
                        // Get a reference to the storage location
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        // Create a reference to the file to upload
                        fileName = uri.getLastPathSegment().substring(6);
                        StorageReference imageRef = storageRef.child("images/" + fileName);
                        Log.d(TAG, "onImagePicked: " + uri.getLastPathSegment().substring(6));
                        //Upload the file to the reference
                        UploadTask uploadTask = imageRef.putFile(uri);

                    }
                });
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_category");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long sizeData = dataSnapshot.getChildrenCount();
                        String name = edNameCate.getText().toString().trim();
                        if (validate()) {
                            CategoryModel cate = new CategoryModel((int) (sizeData + 1), fileName, name);
                            Map<String, Object> mListCate = cate.toMap();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference();
                            Map<String, Object> updates = new HashMap<>();
                            //Toast.makeText(RegisterActivity.this, ""+sizeData, Toast.LENGTH_SHORT).show();
                            updates.put("list_category/" + sizeData, mListCate);
                            reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(AddCategoryActivity.this, "Upload success", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edNameCate.setText("");
            }
        });
    }

    private void init() {
        btnOpenGallery = findViewById(R.id.btnOpenGallery);
        imgCate = findViewById(R.id.imgCate);
        edNameCate = findViewById(R.id.edNameCate);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private boolean validate() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.onActivityResult(requestCode, resultCode, data);
    }
}