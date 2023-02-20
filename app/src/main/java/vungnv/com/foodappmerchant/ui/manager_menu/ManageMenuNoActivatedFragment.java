package vungnv.com.foodappmerchant.ui.manager_menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import vungnv.com.foodappmerchant.adapters.ProductsNotActiveAdapter;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.ProductModel;

public class ManageMenuNoActivatedFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnAdd;
    private ImageView imgDelete, imgFilter;
    private EditText edSearch;
    private RecyclerView rcvListDishes;

    private ProductsNotActiveAdapter productsAdapter;
    private List<ProductModel> listProduct;
    private ArrayList<ProductModel> aListProduct = new ArrayList<>();


    public ManageMenuNoActivatedFragment() {
        // Required empty public constructor
    }

    public static ManageMenuNoActivatedFragment newInstance() {
        return new ManageMenuNoActivatedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_menu_no_activated, container, false);

        init(view);

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddProductActivity.class));
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
                if (aListProduct.size() == 0) {
                    return;
                }
                productsAdapter.getFilter().filter(s.toString());
            }
        });
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        listProduct(auth.getUid());
        return view;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_product);
        swipeRefreshLayout.setOnRefreshListener(this);
        btnAdd = view.findViewById(R.id.btnAdd);
        imgDelete = view.findViewById(R.id.imgDelete);
        imgFilter = view.findViewById(R.id.imgFilter);
        edSearch = view.findViewById(R.id.edSearchInMenu);
        rcvListDishes = view.findViewById(R.id.rcvListProductNoActive);

    }

    private void updateStatusMerchant(String idUser, int status) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child("list_user_merchant")
                .child(idUser).child("status");
        ref.setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "update status 1 -> 2 success");
            }
        });
    }

    private void listProduct(String idUser) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product_not_active/" + idUser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListProduct.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductModel model = snapshot1.getValue(ProductModel.class);
                    if (model == null) {
                        return;
                    }
                    if (model.status == -1) {
                        aListProduct.add(model);
                    }

                }

                if (aListProduct.size() == 0) {
                    setData(aListProduct);
                    updateStatusMerchant(idUser, 1);
                    Toast.makeText(getContext(), NO_PRODUCT, Toast.LENGTH_SHORT).show();
                    return;
                }
                updateStatusMerchant(idUser, 2);
                setData(aListProduct);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setData(ArrayList<ProductModel> aListProduct) {
        productsAdapter = new ProductsNotActiveAdapter(getContext(), aListProduct);
        rcvListDishes.setAdapter(productsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvListDishes.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListDishes.getContext(),
                linearLayoutManager.getOrientation());
        rcvListDishes.addItemDecoration(dividerItemDecoration);
        rcvListDishes.setHasFixedSize(true);
        rcvListDishes.setNestedScrollingEnabled(false);
    }

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