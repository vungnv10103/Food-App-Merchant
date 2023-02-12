package vungnv.com.foodappmerchant.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappmerchant.MainActivity;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.adapters.OrderAdapter;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.OrderDAO;
import vungnv.com.foodappmerchant.dao.UsersDAO;
import vungnv.com.foodappmerchant.model.Order;
import vungnv.com.foodappmerchant.model.UserModel;
import vungnv.com.foodappmerchant.utils.createNotificationChannel;

public class OrderFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rcvListOrder;


    private UsersDAO usersDAO;
    private List<UserModel> listUser;
    private ArrayList<UserModel> aListUser;
    private UserModel itemUser;
    private OrderAdapter orderAdapter;
    private OrderDAO orderDAO;
    private List<Order> listOrder;

    private SpotsDialog processDialog;
    private final long delay = 1000;
    private int temp = 0;
    private final int waitingTime = 20;
    private ArrayList<Order> aListOrder = new ArrayList<>();

    createNotificationChannel notification = new createNotificationChannel();

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
        String idMerchant = auth.getUid();
        getListOrder(idMerchant);
        //refreshList(idMerchant);
        refreshWaitingTime();


        return view;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_home);
        swipeRefreshLayout.setOnRefreshListener(this);
        usersDAO = new UsersDAO(getContext());
        orderDAO = new OrderDAO(getContext());
        rcvListOrder = view.findViewById(R.id.rcvListOrder);
        processDialog = new SpotsDialog(getContext(), R.style.Custom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification.createNotificationChannel(requireContext());
        }
    }

    private void getListOrder(String idMerchant) {

        processDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListOrder.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    assert order != null;
                    vungnv.com.foodappmerchant.utils.createNotification.mCreateNotification(getContext(), "Có đơn hàng mới", order.dateTime.toString());
                    aListOrder.add(order);
                }

                setData(aListOrder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
                processDialog.dismiss();
            }
        });
    }

    private void setData(ArrayList<Order> aListOrder) {

        if (aListOrder.size() == 0) {
            processDialog.dismiss();
            orderAdapter = new OrderAdapter(getContext(), aListOrder);
            rcvListOrder.setAdapter(orderAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            rcvListOrder.setLayoutManager(linearLayoutManager);
//            if (temp==0){
               //Toast.makeText(getContext(), "Hiện không có đơn nào !", Toast.LENGTH_SHORT).show();
//            }
          //  temp++;

            return;
        }
        for (int i = 0; i < aListOrder.size(); i++) {
            Order item = new Order();
            Order value = aListOrder.get(i);
            item.id = value.id;
            item.idUser = value.idUser;
            item.dateTime = value.dateTime;
            item.waitingTime = waitingTime; //600
            item.items = value.items;
            item.quantity = value.quantity;
            item.status = value.status;
            item.price = value.price;
            item.notes = value.notes;
            if (orderDAO.insert(item) > 0) {
                Log.d(TAG, "save data to local db order success");
            } else {
                Log.d(TAG, "save data to local db order fail or item exist");
            }
        }
        listOrder = orderDAO.getALLDefault();


        orderAdapter = new OrderAdapter(getContext(), listOrder);
        rcvListOrder.setAdapter(orderAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvListOrder.setLayoutManager(linearLayoutManager);
        processDialog.dismiss();
    }

    private void refreshWaitingTime() {
        Thread t1 = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                listOrder = orderDAO.getALLDefault();
                while (true) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            String idOrder = "";
                            int waitingTime = 0;
                            for (int i = 0; i < listOrder.size(); i++) {
                                idOrder = listOrder.get(i).id;
                                //Log.d(TAG, "idOrder: " + idOrder);
                                waitingTime = orderDAO.getWaitingTime(idOrder);
                                waitingTime--;
                                if (waitingTime == 0) {
                                    if (orderDAO.deleteOrderOutOfTime(idOrder) > 0) {
                                        Log.d(TAG, "delete order out of time success");
                                    } else {
                                        Log.d(TAG, "delete order out of time fail");
                                    }
                                }


                                Order item = new Order();
                                item.id = idOrder;
                                item.waitingTime = waitingTime;
                                if (orderDAO.updateWaitingTime(item) > 0) {
                                    Log.d(TAG, "update time success with idOrder: " + idOrder + " current time: " + waitingTime);
                                } else {
                                    Log.d(TAG, "update time fail");
                                }
                            }
                            aListOrder = (ArrayList<Order>) orderDAO.getALLDefault();
                            setData(aListOrder);

                        }
                    });


                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
    }


//    private void refreshList(String idMerchant) {
//        Thread t1 = new Thread() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void run() {
//                while (true) {
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            processDialog.show();
//                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
//                            ref.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    // aListOrder.clear();
//                                    for (DataSnapshot ds : snapshot.getChildren()) {
//                                        Order order = ds.getValue(Order.class);
//                                        assert order != null;
//                                        vungnv.com.foodappmerchant.utils.createNotification.mCreateNotification(getContext(), "Có đơn hàng mới", order.dateTime.toString());
//                                        aListOrder.add(order);
//                                    }
//                                    if (aListOrder.size() == 0) {
//                                        processDialog.dismiss();
//                                        orderAdapter = new OrderAdapter(getContext(), aListOrder);
//                                        rcvListOrder.setAdapter(orderAdapter);
//                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
//                                        rcvListOrder.setLayoutManager(linearLayoutManager);
//                                        Toast.makeText(getContext(), "Hiện không có đơn nào !", Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//
//                                    orderAdapter = new OrderAdapter(getContext(), aListOrder);
//                                    rcvListOrder.setAdapter(orderAdapter);
//                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
//                                    rcvListOrder.setLayoutManager(linearLayoutManager);
//                                    processDialog.dismiss();
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    processDialog.dismiss();
//                                }
//                            });
//                        }
//                    });
//
//
//                    try {
//                        Thread.sleep(delay);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        t1.start();
//    }


    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                // reload list order
                //Toast.makeText(getContext(), UPDATE_DATA, Toast.LENGTH_SHORT).show();

            }
        }, 1500);
    }
}