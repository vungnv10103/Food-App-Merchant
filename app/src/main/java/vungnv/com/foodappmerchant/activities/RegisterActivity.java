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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappmerchant.MainActivity;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.UsersDAO;
import vungnv.com.foodappmerchant.model.CategoryModel;
import vungnv.com.foodappmerchant.model.UserModel;
import vungnv.com.foodappmerchant.utils.ImagePicker;
import vungnv.com.foodappmerchant.utils.NetworkChangeListener;

public class RegisterActivity extends AppCompatActivity implements Constant {
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private ArrayList<CategoryModel> aListCate;
    private ImageView imgMerchant;
    private EditText edName, edNameRestaurant, edEmail, edPhone, edAddress;
    private Button btnGallery, btnOpenCam, btnRegister;
    private TextView tvSignIn;

    private UsersDAO usersDAO;
    private UserModel itemUser;
    private final List<UserModel> listUser = new ArrayList<>();

    private String fileName = "";
    private String mLocation = "";
    private String coordinates = "";
    private SpotsDialog progressDialog;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final Set<String> usedSequences = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.pickImage(RegisterActivity.this, new ImagePicker.OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri uri) {
                        imgMerchant.setImageURI(uri);
                        // Get a reference to the storage location
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        // Create a reference to the file to upload
                        fileName = uri.getLastPathSegment().substring(6) + ".png";
                        StorageReference imageRef = storageRef.child("images_users/" + fileName);
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
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PIC_REQUEST);
                    } else {
                        // Permission has already been granted, open the camera
                        openCamera();

                    }
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setCancelable(false);
                getLastLocation();
                String name = edName.getText().toString().trim();
                String nameRestaurant = edNameRestaurant.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                String phoneNumber = edPhone.getText().toString().trim();
                String address = edAddress.getText().toString().trim();
                Log.d(TAG, "result coordinate: " + coordinates);

                UserModel userModel = new UserModel("", 0, fileName, name, email,
                        "", phoneNumber, nameRestaurant, coordinates, address, "");
                Map<String, Object> mListUser = userModel.toMap();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                Map<String, Object> updates = new HashMap<>();

                int index = email.indexOf("@");
                String userName = email.substring(0, index);

                updates.put("list_user_merchant_default/" + userName, mListUser);
                reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(RegisterActivity.this, REQUEST_FORM, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finishAffinity();
                    }
                });


            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });
    }

    private void init() {
        usersDAO = new UsersDAO(getApplicationContext());
        tvSignIn = findViewById(R.id.tvSignIn);
        imgMerchant = findViewById(R.id.imgMerchant);
        edName = findViewById(R.id.edNameMerchant);
        edNameRestaurant = findViewById(R.id.edRestaurantName);
        edEmail = findViewById(R.id.edEmail);
        edPhone = findViewById(R.id.edPhoneNumber);
        edAddress = findViewById(R.id.edAddress);
        btnGallery = findViewById(R.id.btnGallery);
        btnOpenCam = findViewById(R.id.btnOpenCamera);
        btnRegister = findViewById(R.id.btnRegister);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        progressDialog = new SpotsDialog(RegisterActivity.this, R.style.Custom);
    }

    private boolean validate() {
        return true;
    }

    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            Toast.makeText(RegisterActivity.this, WRONG_EMAIL_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkPhoneNumber(String phoneNumber) {
        String regex = "^(03|08)[0-9]{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()) {
            Toast.makeText(getApplicationContext(), WRONG_PHONE_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkAccountExist(String idUser) {
        int temp = 0;
        //listUser = (ArrayList<UserModel>) usersDAO.getALL();
        for (UserModel item : listUser) {
            if (idUser.toLowerCase(Locale.ROOT).equals(item.id.toLowerCase(Locale.ROOT))) {
                temp++;
            }
        }
        return temp <= 0;
    }


    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                    edLocation.setText("" + addresses.get(0).getAddressLine(0));
                                    mLocation = addresses.get(0).getAddressLine(0);
                                    double currentLongitude = addresses.get(0).getLongitude();
                                    double currentLatitude = addresses.get(0).getLatitude();
                                    coordinates = currentLongitude + "-" + currentLatitude;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {

            askPermission();

        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private static String generateUniqueRandomSequence(int length) {
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
            Toast.makeText(RegisterActivity.this, CHECK_CAM, Toast.LENGTH_SHORT).show();
            return false;
        }
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
            imgMerchant.setImageBitmap(bitmap);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images_users");

            // Create a file from the Bitmap
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();

            // Create a reference to the image file in Firebase Storage
            fileName = generateUniqueRandomSequence(10) + ".png";
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
                    Log.d(TAG, "Upload is " + progress + "% done");
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