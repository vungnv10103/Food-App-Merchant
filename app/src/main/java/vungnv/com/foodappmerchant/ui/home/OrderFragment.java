package vungnv.com.foodappmerchant.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.DishesDAO;
import vungnv.com.foodappmerchant.dao.UsersDAO;
import vungnv.com.foodappmerchant.model.DishesModel;
import vungnv.com.foodappmerchant.model.UserModel;

public class OrderFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private DishesDAO dishesDAO;
    private List<DishesModel> listDishes;
    private DishesModel itemDishes;

    private UsersDAO usersDAO;
    private List<UserModel> listUser;
    private UserModel itemUser;


    public OrderFragment() {

    }
    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        init(view);
        pushData();
        pushDataUserMerchant();
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));
        return view;
    }
    private void init(View view){
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_home);
        swipeRefreshLayout.setOnRefreshListener(this);
        dishesDAO = new DishesDAO(getContext());
        usersDAO = new UsersDAO(getContext());
    }
    private void pushData() {
        int temp = 0;
        String name = "Cơm";
        String img = "1000002637";

        listDishes = dishesDAO.getALL();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference reference = database.getReference("list_demo");
        String key = reference.child("list_demo").push().getKey();
        reference.setValue(listDishes, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getContext(), "Thàng công", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void pushDataUserMerchant() {

        itemUser = new UserModel();

        itemUser.id = "";
        itemUser.status = 0;
        itemUser.img = "1000002633";
        itemUser.name = "Nguyen van Vung 7";
        itemUser.email = "vung123@gmail.com";
        itemUser.pass = "";
        itemUser.phoneNumber = "0987654321";
        itemUser.restaurantName = "Cơm quán";
        itemUser.address = "Cổ nhuế 2, Ha Noi";
        itemUser.coordinates = "105.77553463-21.06693654";
        itemUser.feedback = "";
        if (usersDAO.insert(itemUser) > 0){
            Log.d(TAG, "insert data to db user_merchant success: ");
        }

//        String key = reference.child("list_demo").push().getKey();
        listUser = usersDAO.getALL();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        Map<String, Object> updates = new HashMap<>();
        updates.put("list_user_merchant", listUser);
        reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
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
               // reload list order
                Toast.makeText(getContext(), UPDATE_DATA, Toast.LENGTH_SHORT).show();

            }
        }, 1500);
    }
}