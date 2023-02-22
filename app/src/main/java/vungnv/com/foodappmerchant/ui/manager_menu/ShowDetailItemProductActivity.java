package vungnv.com.foodappmerchant.ui.manager_menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.activities.LoginActivity;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.CategoryModel;
import vungnv.com.foodappmerchant.model.Product;
import vungnv.com.foodappmerchant.model.ProductModel;
import vungnv.com.foodappmerchant.utils.ImagePicker;
import vungnv.com.foodappmerchant.utils.NetworkChangeListener;

public class ShowDetailItemProductActivity extends AppCompatActivity implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private TextView tvDemo, tvNameProduct;
    private ImageView imgProduct;
    private EditText edName, edPrice, edDiscount, edTime, edDesc;
    private AutoCompleteTextView edCate;
    private ImageButton imgStatus;

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private SpotsDialog progressDialog;

    private Button btnSave;
    private TextView tvDelete;
    private String fileName = "";
    private int temp = 0;
    private String id = "";


    private ArrayList<CategoryModel> aListCate;
    private final ArrayList<Product> aListProducts = new ArrayList<>();
    ArrayAdapter<String> adapterItems;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_item_dishes);

        init();
        listCate();

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("Không có sẵn (ẩn trên app)", 0);
        map.put("Tạm hết hàng", 1);
        map.put("Có sẵn", 2);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));
        tvDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Toast.makeText(ShowDetailItemProductActivity.this, "updating...", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data-type");
        if (bundle != null) {
            int pos = bundle.getInt("pos");
            int status = bundle.getInt("status");
            String name = bundle.getString("name");
            if (status == -1) {
                getDataNoActive(name);
            } else {
                getData(pos);
            }


        }

        // bottom sheet status

        imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert bundle != null;
                int status = bundle.getInt("status");
                if (status == -1) {
                    Toast.makeText(ShowDetailItemProductActivity.this, PRODUCT_NOT_ACTIVATED, Toast.LENGTH_SHORT).show();
                    return;
                }
                Dialog dialog = new Dialog(ShowDetailItemProductActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_sheet);
                dialog.setCancelable(false);

                ImageButton imgClose = dialog.findViewById(R.id.imgClose);
                ImageButton imgSave = dialog.findViewById(R.id.imgSave);
                RadioButton rb1 = dialog.findViewById(R.id.rbTempOutStock);
                RadioButton rb0 = dialog.findViewById(R.id.rbNotAvailable);
                RadioButton rb2 = dialog.findViewById(R.id.rbAvailable);
                int pos = bundle.getInt("pos");
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("list_product/" + auth.getUid()).child(String.valueOf(pos));
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProductModel model = snapshot.getValue(ProductModel.class);
                        assert model != null;
                        int status = model.status;
                        switch (status) {
                            case 0:
                                rb0.setChecked(true);
                                break;
                            case 1:
                                rb1.setChecked(true);
                                break;
                            case 2:
                                rb2.setChecked(true);
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = dialog.findViewById(checkedId);
                        String value = radioButton.getText().toString();


                    }
                });
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // update status in list_product_all
                String path = "list_product_all";
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        aListProducts.clear();
                        for (DataSnapshot childSnapshot1 : snapshot.getChildren()) {
                            Product value = childSnapshot1.getValue(Product.class);
                            assert value != null;
                            if (value.id.equals(id)) {
                                aListProducts.add(value);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                imgSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // change status
                        int pos = bundle.getInt("pos");

                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = dialog.findViewById(selectedId);
                        int status = map.get(radioButton.getText().toString());
                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference().child("list_product")
                                .child(Objects.requireNonNull(auth.getUid())).child(String.valueOf(pos)).child("status");
                        ref.setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "update status in list_product success");
                                Toast.makeText(ShowDetailItemProductActivity.this, UPDATE_STATUS_SUCCESS, Toast.LENGTH_SHORT).show();

                                int pos = aListProducts.get(0).pos;
                                DatabaseReference ref = FirebaseDatabase.getInstance()
                                        .getReference().child("list_product_all")
                                        .child(String.valueOf(pos)).child("status");
                                ref.setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "update status in list_product_all success");
                                    }
                                });

                            }
                        });

                        dialog.dismiss();
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });

        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.pickImage(ShowDetailItemProductActivity.this, new ImagePicker.OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri uri) {
                        progressDialog.show();
                        // Get a reference to the storage location
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        // Create a reference to the file to upload
                        fileName = uri.getLastPathSegment().substring(6);
                        StorageReference imageRef = storageRef.child("images_product/" + fileName);

                        Log.d(TAG, "onImagePicked: " + uri.getLastPathSegment().substring(6));
                        //Upload the file to the reference
                        UploadTask uploadTask = imageRef.putFile(uri);
                        imgProduct.setImageURI(uri);
                        temp++;
                        progressDialog.dismiss();


                    }
                });
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name = edName.getText().toString().trim();
                String type = edCate.getText().toString().trim();
                String price = edPrice.getText().toString().trim();
                String discount = edDiscount.getText().toString().trim();
                String time = edTime.getText().toString().trim();
                String desc = edDesc.getText().toString().trim();

                assert bundle != null;
                int pos = bundle.getInt("pos");
                int status = bundle.getInt("status");
                Log.d(TAG, "temp: " + temp);
                if (temp > 0) {
                    if (status == -1) {
                        updateData(fileName, name, type, Double.parseDouble(price), Double.parseDouble(discount), time, desc);
                    } else {
                        updateData(pos, fileName, name, type, Double.parseDouble(price), Double.parseDouble(discount), time, desc);
                    }


                } else {
                    if (status == -1) {
                        updateData(name, type, Double.parseDouble(price), Double.parseDouble(discount), time, desc);
                    } else {
                        updateData(pos, name, type, Double.parseDouble(price), Double.parseDouble(discount), time, desc);
                    }


                }

            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ShowDetailItemProductActivity.this);
                dialog.setContentView(R.layout.dialog_confirm_delete);
                dialog.setCancelable(false);
                Button btnCancel, btnConfirm;

                btnCancel = dialog.findViewById(R.id.btnCancel);
                btnConfirm = dialog.findViewById(R.id.btnConfirm);
                btnConfirm.setOnClickListener(v1 -> {
                    //  remove item
                    dialog.dismiss();
                });
                btnCancel.setOnClickListener(v12 -> dialog.dismiss());
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });
    }

    private void init() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_dish);
        swipeRefreshLayout.setOnRefreshListener(this);
        toolbar = findViewById(R.id.toolBarManageMenu);
        tvDemo = findViewById(R.id.tvDemo);
        tvNameProduct = findViewById(R.id.tvNameProduct);
        imgProduct = findViewById(R.id.imgProduct);
        edName = findViewById(R.id.edNameProduct);
        edCate = findViewById(R.id.edCategory);
        edPrice = findViewById(R.id.edPrice);
        edDiscount = findViewById(R.id.edDiscount);
        edTime = findViewById(R.id.edTime);
        edDesc = findViewById(R.id.edDesc);
        imgStatus = findViewById(R.id.imgStatus);

        btnSave = findViewById(R.id.btnSave);
        tvDelete = findViewById(R.id.tvDelete);
        progressDialog = new SpotsDialog(ShowDetailItemProductActivity.this, R.style.Custom);


    }

    private void setImageProduct(String idImage) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("images_product/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                // Load the image using Glide
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imgProduct);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "get image from firebase: " + exception.getMessage());
            }
        });
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
                    Toast.makeText(ShowDetailItemProductActivity.this, ERROR_FETCHING_DATE + CATEGORY_LIST, Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] listCate = new String[aListCate.size()];
                //Toast.makeText(AddProductActivity.this, "list size: " + aListCate.size(), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < aListCate.size(); i++) {
                    String temp = aListCate.get(i).name;
                    listCate[i] = temp;
                }
                adapterItems = new ArrayAdapter<String>(ShowDetailItemProductActivity.this, R.layout.list_item_cate, listCate);
                edCate.setAdapter(adapterItems);
                edCate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = aListCate.get(position).name;
                        edCate.setText(name);
                        if (edCate.getText().toString().trim().length() > 0) {
                            edCate.setOnItemClickListener(null);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }

    private void getData(int pos) {
        progressDialog.show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product/" + auth.getUid()).child(String.valueOf(pos));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductModel model = snapshot.getValue(ProductModel.class);
                assert model != null;
                id = model.id;
                String img = model.img;
                String name = model.name;
                String type = model.type;
                Double price = model.price;
                Double discount = model.discount;
                String time = model.timeDelay;
                String desc = model.description;
                tvNameProduct.setText(name);
                setImageProduct(img);
                edName.setText(name);
                edCate.setText(type);
                edPrice.setText(String.valueOf(price));
                edDiscount.setText(String.valueOf(discount));
                edTime.setText(time);
                edDesc.setText(desc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progressDialog.dismiss();
    }

    private void getDataNoActive(String name) {
        progressDialog.show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product_not_active/" + auth.getUid()).child(name);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductModel model = snapshot.getValue(ProductModel.class);
                assert model != null;
                String img = model.img;
                String name = model.name;
                String type = model.type;
                Double price = model.price;
                Double discount = model.discount;
                String time = model.timeDelay;
                String desc = model.description;
                tvNameProduct.setText(name);
                setImageProduct(img);
                edName.setText(name);
                edCate.setText(type);
                edPrice.setText(String.valueOf(price));
                edDiscount.setText(String.valueOf(discount));
                edTime.setText(time);
                edDesc.setText(desc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progressDialog.dismiss();
    }

    private void updateData(int pos, String img, String name, String type, Double price, Double discount, String time, String desc) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product/" + auth.getUid()).child(String.valueOf(pos));
        Map<String, Object> productUpdates = new HashMap<>();
        productUpdates.put("img", img);
        productUpdates.put("name", name);
        productUpdates.put("type", type);
        productUpdates.put("price", price);
        productUpdates.put("discount", discount);
        productUpdates.put("timeDelay", time);
        productUpdates.put("description", desc);
        databaseReference.updateChildren(productUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(ShowDetailItemProductActivity.this, UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void updateData(int pos, String name, String type, Double price, Double discount,
                            String time, String desc) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product/" + auth.getUid()).child(String.valueOf(pos));
        Map<String, Object> productUpdates = new HashMap<>();
        productUpdates.put("name", name);
        productUpdates.put("type", type);
        productUpdates.put("price", price);
        productUpdates.put("discount", discount);
        productUpdates.put("timeDelay", time);
        productUpdates.put("description", desc);
        databaseReference.updateChildren(productUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(ShowDetailItemProductActivity.this, UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void updateData(String img, String name, String type, Double price, Double discount,
                            String time, String desc) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product_not_active/" + auth.getUid()).child(name);
        Map<String, Object> productUpdates = new HashMap<>();
        productUpdates.put("img", img);
        productUpdates.put("name", name);
        productUpdates.put("type", type);
        productUpdates.put("price", price);
        productUpdates.put("discount", discount);
        productUpdates.put("timeDelay", time);
        productUpdates.put("description", desc);
        databaseReference.updateChildren(productUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(ShowDetailItemProductActivity.this, UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void updateData(String name, String type, Double price, Double discount, String time, String desc) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product_not_active/" + auth.getUid()).child(name);
        Map<String, Object> productUpdates = new HashMap<>();
        productUpdates.put("name", name);
        productUpdates.put("type", type);
        productUpdates.put("price", price);
        productUpdates.put("discount", discount);
        productUpdates.put("timeDelay", time);
        productUpdates.put("description", desc);
        databaseReference.updateChildren(productUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(ShowDetailItemProductActivity.this, UPDATE_SUCCESS, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                // refresh data

            }
        }, 1500);
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