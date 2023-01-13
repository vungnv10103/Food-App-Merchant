package vungnv.com.foodappmerchant.ui.manager_menu;

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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.activities.AddProductActivity;
import vungnv.com.foodappmerchant.adapters.ListOfDishesAdapter;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.DishesDAO;
import vungnv.com.foodappmerchant.model.DishesModel;

public class ManageMenuActivity extends AppCompatActivity implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private TextView tvAdd;
    private ImageView imgDelete, imgFilter;
    private EditText edSearch;
    private RecyclerView rcvListDishes;

    private DishesDAO dishesDAO;
    private ArrayList<DishesModel> listDishes;
    private ListOfDishesAdapter listOfDishesAdapter;
    private DishesModel itemDishes;

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
                if (listDishes.size() == 0){
                    return;
                }
                listOfDishesAdapter.getFilter().filter(s.toString());
            }
        });
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ManageMenuActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
       // insertDefault();
        listDishes();
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
        dishesDAO = new DishesDAO(getApplicationContext());

    }

    private void insertDefault() {
        itemDishes = new DishesModel();
        itemDishes.name = "Phở";
        if (dishesDAO.insert(itemDishes) > 0) {
            Log.d(TAG, "insert data to db Dishes success: ");
        }
    }

    private void listDishes() {

        listDishes = (ArrayList<DishesModel>) dishesDAO.getALL();
        if (listDishes.size() == 0) {
            return;
        }
        listOfDishesAdapter = new ListOfDishesAdapter(this, listDishes);
        rcvListDishes.setAdapter(listOfDishesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
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
                listDishes();

            }
        }, 1500);
    }
}