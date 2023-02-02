package vungnv.com.foodappmerchant.ui.manager_menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappmerchant.MainActivity;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.activities.AddProductActivity;
import vungnv.com.foodappmerchant.adapters.ProductsAdapter;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.ProductDAO;
import vungnv.com.foodappmerchant.model.ProductModel;

public class ManageMenuActivatedFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imgDelete, imgFilter;
    private EditText edSearch;
    private RecyclerView rcvListDishes;

    private ProductDAO productDAO;
    private ProductsAdapter productsAdapter;
    private List<ProductModel> listProduct;
    private ArrayList<ProductModel> aListProduct = new ArrayList<>();

    private SpotsDialog progressDialog;


    public ManageMenuActivatedFragment() {
        // Required empty public constructor
    }

    public static ManageMenuActivatedFragment newInstance() {
        return new ManageMenuActivatedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(MANAGE_MENUS);
        View view = inflater.inflate(R.layout.fragment_manage_menu_activated, container, false);

        init(view);
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));
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
        progressDialog.dismiss();
        return view;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_product);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgDelete = view.findViewById(R.id.imgDelete);
        imgFilter = view.findViewById(R.id.imgFilter);
        edSearch = view.findViewById(R.id.edSearchInMenu);
        rcvListDishes = view.findViewById(R.id.rcvListProductActivated);
        productDAO = new ProductDAO(getContext());

        progressDialog = new SpotsDialog(getContext(), R.style.Custom);

    }

    private void listProduct(String idUser) {
        progressDialog.show();
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
                    if (model.status == 2) {
                        aListProduct.add(model);
                    }

                }

                if (aListProduct.size() == 0) {
                    Toast.makeText(getContext(), NO_PRODUCT, Toast.LENGTH_SHORT).show();
                    return;
                }
                productsAdapter = new ProductsAdapter(getContext(), aListProduct);
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
            public void onCancelled(@NonNull DatabaseError error) {

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
                FirebaseAuth auth = FirebaseAuth.getInstance();
                listProduct(auth.getUid());
                progressDialog.dismiss();

            }
        }, 1500);
    }
}