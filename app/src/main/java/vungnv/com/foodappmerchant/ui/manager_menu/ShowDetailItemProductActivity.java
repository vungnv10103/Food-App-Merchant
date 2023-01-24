package vungnv.com.foodappmerchant.ui.manager_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.activities.AddProductActivity;
import vungnv.com.foodappmerchant.adapters.StatusProductAdapter;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.StatusProductDAO;
import vungnv.com.foodappmerchant.model.CategoryModel;
import vungnv.com.foodappmerchant.model.StatusProductModel;

public class ShowDetailItemProductActivity extends AppCompatActivity implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private TextView tvDemo, tvNameProduct;
    private ImageView imgProduct;
    private EditText edName, edPrice, edTime, edDesc;
    private AutoCompleteTextView edCate;
    private ImageButton imgStatus;

    private Button btnSave;
    private TextView tvCancel;


    private List<StatusProductModel> listStatus;
    private StatusProductDAO statusProductDAO;

    private ArrayList<CategoryModel> aListCate;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_item_dishes);

        init();
        // insertDefault();
        listCate();
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
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
            Map<Integer, String> map = new HashMap<Integer, String>();
            map.put(0, "Không có sẵn (ẩn trên app)");
            map.put(1, "Tạm hết hàng");
            map.put(2, "Có sẵn");

            int status = bundle.getInt("status");

            String img = bundle.getString("img");
            String name = bundle.getString("name");
            String type = bundle.getString("type");
            Double price = bundle.getDouble("price");
            String time = bundle.getString("time");
            String desc = bundle.getString("desc");
            tvNameProduct.setText(name);
            setImageProduct(img);
            edName.setText(name);
            edCate.setText(type);
            edPrice.setText(String.valueOf(price));
            edTime.setText(time);
            edDesc.setText(desc);
        }

        // bottom sheet status
        imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ShowDetailItemProductActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_sheet);
                dialog.setCancelable(false);

                listStatus = statusProductDAO.getALL();
                if (listStatus.size() == 0) {
                   // Toast.makeText(ShowDetailItemProductActivity.this, "", Toast.LENGTH_SHORT).show();
                    return;
                }
                StatusProductAdapter statusProductAdapter = new StatusProductAdapter(ShowDetailItemProductActivity.this, listStatus);
                RecyclerView rcvChangeStatus = dialog.findViewById(R.id.rcvChangeStatus);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowDetailItemProductActivity.this, RecyclerView.VERTICAL, false);
                rcvChangeStatus.setLayoutManager(linearLayoutManager);
                rcvChangeStatus.setAdapter(statusProductAdapter);
                ImageButton imgClose = dialog.findViewById(R.id.imgClose);
                ImageButton imgSave = dialog.findViewById(R.id.imgSave);
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                imgSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ShowDetailItemProductActivity.this, "saved ...", Toast.LENGTH_SHORT).show();
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
        edTime = findViewById(R.id.edTime);
        edDesc = findViewById(R.id.edDesc);
        imgStatus = findViewById(R.id.imgStatus);
        statusProductDAO = new StatusProductDAO(getApplicationContext());

        btnSave = findViewById(R.id.btnSave);
        tvCancel = findViewById(R.id.tvCancel);

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

    private void insertDefault() {
        StatusProductModel item = new StatusProductModel();
        item.type = "Không có sẵn (Ẩn trên app)";
        item.status = 0;
        if (statusProductDAO.insert(item) > 0) {
            Log.d(TAG, "insert db StatusProduct success ");
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
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
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
}