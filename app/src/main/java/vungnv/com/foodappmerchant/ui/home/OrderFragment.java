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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.UsersDAO;
import vungnv.com.foodappmerchant.model.UserModel;

public class OrderFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;


    private UsersDAO usersDAO;
    private List<UserModel> listUser;
    private ArrayList<UserModel> aListUser;
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
        //syncData();
        //pushData();
        //pushDataUserMerchant();
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));
        return view;
    }
    private void init(View view){
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_home);
        swipeRefreshLayout.setOnRefreshListener(this);
        usersDAO = new UsersDAO(getContext());
    }
    private void syncData() {
        aListUser = new ArrayList<>();
        usersDAO.deleteTable();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_user_merchant");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UserModel model = snapshot1.getValue(UserModel.class);
                    assert model != null;
                    // get account not active
                    if (model.status == 0) {
                        aListUser.add(model);
                    }

                }

                if (aListUser.size() == 0) {
                    return;
                }

                // add data to db local
                listUser = usersDAO.getALL();
                for (int i = listUser.size(); i < aListUser.size(); i++) {
                    // Log.d(TAG, "name: " + aListProducts.get(i).name);
                    UserModel item = aListUser.get(i);
                    itemUser = new UserModel();
                    itemUser.id = item.id;
                    itemUser.status = item.status;
                    itemUser.img = item.img;
                    itemUser.name = item.name;
                    itemUser.email = item.email;
                    itemUser.pass = item.pass;
                    itemUser.phoneNumber = item.phoneNumber;
                    itemUser.restaurantName = item.restaurantName;
                    itemUser.address = item.address;
                    itemUser.coordinates = item.coordinates;
                    itemUser.feedback = item.feedback;
                    if (usersDAO.insert(itemUser) > 0) {
                        Log.d(TAG, "update db user_merchant success ");
                    }
                }
                Toast.makeText(getContext(), SYNC_SUCCESS, Toast.LENGTH_SHORT).show();
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
               // reload list order
                Toast.makeText(getContext(), UPDATE_DATA, Toast.LENGTH_SHORT).show();

            }
        }, 1500);
    }
}