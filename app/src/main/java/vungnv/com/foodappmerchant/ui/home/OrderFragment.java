package vungnv.com.foodappmerchant.ui.home;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.adapters.OrderAdapter;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.Order;
import vungnv.com.foodappmerchant.utils.createNotification;
import vungnv.com.foodappmerchant.utils.createNotificationChannel;

public class OrderFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rcvListOrder;
    private OrderAdapter orderAdapter;
    String idMerchant = "";
    private SpotsDialog processDialog;
    createNotificationChannel notification = new createNotificationChannel();
    createNotification createNotification = new createNotification();

    private ValueEventListener valueEventListener;

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

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        idMerchant = auth.getUid();
        getListOrder(idMerchant);

        return view;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_home);
        swipeRefreshLayout.setOnRefreshListener(this);
        rcvListOrder = view.findViewById(R.id.rcvListOrder);
        processDialog = new SpotsDialog(getContext(), R.style.Custom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification.mCreateNotificationChannel(requireContext());
        }
    }

    private void getListOrder(String idMerchant) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> newOrderList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    assert order != null;
                    if (order.status == 1) {
                        newOrderList.add(order);
                    }
                }
                if (newOrderList.isEmpty()) {
                    Toast.makeText(getContext(), "Hết đơn", Toast.LENGTH_SHORT).show();
                    if (orderAdapter != null) {
                        orderAdapter.updateList(newOrderList);
                    }
                } else {
                    if (orderAdapter == null) {
                        orderAdapter = new OrderAdapter(getContext(), newOrderList);
                        rcvListOrder.setAdapter(orderAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        rcvListOrder.setLayoutManager(linearLayoutManager);
                    } else {
                        orderAdapter.updateList(newOrderList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
        valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> newOrderList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    assert order != null;
                    if (order.status == 1) {
                        newOrderList.add(order);
                        Log.d(TAG, "add");
                    }
                }
                if (newOrderList.isEmpty()) {
                    Toast.makeText(getContext(), "Hết đơn", Toast.LENGTH_SHORT).show();
                    if (orderAdapter != null) {
                        orderAdapter.updateList(newOrderList);
                        Log.d(TAG, "add1");
                    }
                } else {
                    if (orderAdapter == null) {
                        orderAdapter = new OrderAdapter(getContext(), newOrderList);
                        rcvListOrder.setAdapter(orderAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        rcvListOrder.setLayoutManager(linearLayoutManager);
                        Log.d(TAG, "add2");
                    } else {
                        orderAdapter.updateList(newOrderList);
                        Log.d(TAG, "add3");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (valueEventListener != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
            ref.removeEventListener(valueEventListener);
        }
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
                getListOrder(idMerchant);


            }
        }, 1500);
    }
}