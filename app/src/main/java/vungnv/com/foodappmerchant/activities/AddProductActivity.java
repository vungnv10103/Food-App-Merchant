package vungnv.com.foodappmerchant.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.ProductDAO;
import vungnv.com.foodappmerchant.model.CategoryModel;
import vungnv.com.foodappmerchant.model.ProductModel;
import vungnv.com.foodappmerchant.model.UserModel;
import vungnv.com.foodappmerchant.utils.ImagePicker;
import vungnv.com.foodappmerchant.utils.NetworkChangeListener;

public class AddProductActivity extends AppCompatActivity implements Constant {
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private ArrayList<CategoryModel> aListCate;
    private ImageView imgProduct;
    private EditText edName, edPrice, edTime, edDesc;
    private Button btnGallery, btnOpenCam, btnSave;
    private TextView tvCancel;
    private AutoCompleteTextView edCate;
    ArrayAdapter<String> adapterItems;
    private String fileName = "";
    private String coordinates = "";

    private ProductModel itemProduct;
    private ProductDAO productDAO;

    private UserModel userModel;
    private ArrayList<UserModel> aListUser;

    private SpotsDialog progressDialog;
    private static final Set<String> usedSequences = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        init();

        listCate();
        // aListCate.size()
        FirebaseAuth auth = FirebaseAuth.getInstance();
        getCoordinate(auth.getUid());
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.pickImage(AddProductActivity.this, new ImagePicker.OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri uri) {
                        imgProduct.setImageURI(uri);
                        // Get a reference to the storage location
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        // Create a reference to the file to upload
                        fileName = uri.getLastPathSegment().substring(6);
                        StorageReference imageRef = storageRef.child("images_product/" + fileName);

                        Log.d(TAG, "onImagePicked: " + uri.getLastPathSegment().substring(6));
                        //Upload the file to the reference
                        UploadTask uploadTask = imageRef.putFile(uri);

                    }
                });
            }
        });
        btnOpenCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraHardware()) {
                    if (ContextCompat.checkSelfPermission(AddProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddProductActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PIC_REQUEST);
                    } else {
                        // Permission has already been granted, open the camera
                        openCamera();

                    }
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setCancelable(false);
                // save data

                String name = edName.getText().toString().trim();
                String cate = edCate.getText().toString().trim();
                double price = Double.parseDouble(edPrice.getText().toString().trim());
                String time = edTime.getText().toString().trim();
                String desc = edDesc.getText().toString().trim();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                getCoordinate(auth.getUid());
                upLoadProduct(auth.getUid(), cate, fileName, name, desc, time, price, 0.0, 0, 0, 2, coordinates, "", 0, 0);
            }
        });
    }

    private void init() {
        imgProduct = findViewById(R.id.imgProduct);
        btnGallery = findViewById(R.id.btnGallery);
        btnOpenCam = findViewById(R.id.btnOpenCamera);
        edName = findViewById(R.id.edNameProduct);
        edCate = findViewById(R.id.edCategory);
        edPrice = findViewById(R.id.edPrice);
        edTime = findViewById(R.id.edTime);
        edDesc = findViewById(R.id.edDesc);
        btnSave = findViewById(R.id.btnSave);
        tvCancel = findViewById(R.id.tvCancel);
        productDAO = new ProductDAO(getApplicationContext());
        progressDialog = new SpotsDialog(AddProductActivity.this, R.style.Custom);
    }

    private boolean validate() {
        return true;
    }

    private String generateRandomNumbers(int length) {
        StringBuilder sb = new StringBuilder();
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(Integer.toString(rand.nextInt(10)));
        }
        return sb.toString();
    }

    public static String generateUniqueRandomSequence(int length) {
        Random rand = new Random();
        String sequence;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(Integer.toString(rand.nextInt(10)));
            }
            sequence = sb.toString();
        } while (usedSequences.contains(sequence));
        usedSequences.add(sequence);
        return sequence;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }

    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            Toast.makeText(AddProductActivity.this, CHECK_CAM, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void listCate() {
        aListCate = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryModel data = dataSnapshot.getValue(CategoryModel.class);
                    aListCate.add(data);
                }
                if (aListCate.size() == 0) {
                    Toast.makeText(AddProductActivity.this, ERROR_FETCHING_DATE + CATEGORY_LIST, Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] listCate = new String[aListCate.size()];
                //Toast.makeText(AddProductActivity.this, "list size: " + aListCate.size(), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < aListCate.size(); i++) {
                    String temp = aListCate.get(i).name;
                    listCate[i] = temp;
                }
                adapterItems = new ArrayAdapter<String>(AddProductActivity.this, R.layout.list_item_cate, listCate);
                edCate.setAdapter(adapterItems);
                edCate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = aListCate.get(position).name;
                        edCate.setText(name);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }

    private void getCoordinate(String idUser) {
        aListUser = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_user_merchant/" + idUser + "/coordinates");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get db from firebase
                coordinates = Objects.requireNonNull(snapshot.getValue()).toString();
                Log.d(TAG, "result coordinate: " + coordinates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearForm() {
        imgProduct.setImageResource(R.drawable.default_thumbnail);
        edName.setText("");
        edCate.setText("");
        edPrice.setText("");
        edTime.setText("");
        edDesc.setText("");
    }


    private void upLoadProduct(String idUser, String type, String img, String name, String description,
                               String timeDelay, double price, double rate, int favourite, int check, int status,
                               String coordinate, String feedBack, int quantity_sold, int quantityTotal) {

        ProductModel user = new ProductModel(idUser, type, img, name, description, timeDelay, price,
                rate, favourite, check, status, coordinate, feedBack, quantity_sold, quantityTotal);
        Map<String, Object> mListProduct = user.toMap();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        Map<String, Object> updates = new HashMap<>();

        updates.put("list_product_not_active/" + idUser + "/" + name, mListProduct);
        reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(AddProductActivity.this, REQUEST_FORM, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                clearForm();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, ERROR_CAM, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == CAMERA_PIC_REQUEST) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap == null) {
                return;
            }
            Log.d(TAG, "bitmap resource: " + bitmap);
            imgProduct.setImageBitmap(bitmap);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images_product");

            // Create a file from the Bitmap
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();

            // Create a reference to the image file in Firebase Storage
            fileName = generateUniqueRandomSequence(10);
            StorageReference imageRef = imagesRef.child(fileName);

            // Upload the file to Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(data1);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads
                }
            });
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            });


        }
    }

    private void getPos(String idUser) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_user_merchant/" + idUser);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long childCount = dataSnapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}