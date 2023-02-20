package vungnv.com.foodappmerchant.ui.manager_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.activities.AddProductActivity;
import vungnv.com.foodappmerchant.adapters.ProductsAdapter;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.ProductModel;

public class ManageMenuActivity extends AppCompatActivity implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private TextView tvAdd;
    private ImageView imgDelete, imgFilter;
    private EditText edSearch;
    private RecyclerView rcvListDishes;

    private ProductsAdapter productsAdapter;
    private List<ProductModel> listProduct;
    private ArrayList<ProductModel> aListProduct = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_menu);

        init();

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
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                startActivity(new Intent(ManageMenuActivity.this, AddProductActivity.class));
            }
        });
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // filter
                if (s.length() > 0) {

                    imgDelete.setVisibility(View.VISIBLE);
                    imgDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            edSearch.setText("");
                        }
                    });
                } else {
                    imgDelete.setVisibility(View.INVISIBLE);
                }
                if (aListProduct.size() == 0){
                    return;
                }
                productsAdapter.getFilter().filter(s.toString());
            }
        });
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ManageMenuActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        listProduct(auth.getUid());
    }

    private void init() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_list_product);
        swipeRefreshLayout.setOnRefreshListener(this);
        toolbar = findViewById(R.id.toolBarManageMenu);
        tvAdd = findViewById(R.id.tvAdd);
        imgDelete = findViewById(R.id.imgDelete);
        imgFilter = findViewById(R.id.imgFilter);
        edSearch = findViewById(R.id.edSearchInMenu);
        rcvListDishes = findViewById(R.id.rcvListOfDishes);

    }


    private void listProduct(String idUser) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product/" + idUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListProduct.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductModel model = snapshot1.getValue(ProductModel.class);
                    if (model == null) {
                        return;
                    }
                    aListProduct.add(model);
                }

                if (aListProduct.size() == 0) {
                    Toast.makeText(ManageMenuActivity.this, NO_PRODUCT, Toast.LENGTH_SHORT).show();
                    return;
                }
                productsAdapter = new ProductsAdapter(ManageMenuActivity.this, aListProduct);
                rcvListDishes.setAdapter(productsAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ManageMenuActivity.this, RecyclerView.VERTICAL, false);
                rcvListDishes.setLayoutManager(linearLayoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListDishes.getContext(),
                        linearLayoutManager.getOrientation());
                rcvListDishes.addItemDecoration(dividerItemDecoration);
                rcvListDishes.setHasFixedSize(true);
                rcvListDishes.setNestedScrollingEnabled(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

//    private void listProductLocal() {
//        listProduct = productDAO.getALLDefault();
//        if (listProduct.size() == 0) {
//            Toast.makeText(this, "Hiện không có sản phẩm nào !!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        productsAdapter = new ProductsAdapter(this, listProduct);
//        rcvListDishes.setAdapter(productsAdapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
//        rcvListDishes.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListDishes.getContext(),
//                linearLayoutManager.getOrientation());
//        rcvListDishes.addItemDecoration(dividerItemDecoration);
//        rcvListDishes.setHasFixedSize(true);
//        rcvListDishes.setNestedScrollingEnabled(false);
//
//    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                listProduct(auth.getUid());

            }
        }, 1500);
    }
}