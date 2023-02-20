package vungnv.com.foodappmerchant.ui.home;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import vungnv.com.foodappmerchant.dao.OrderDAO;
import vungnv.com.foodappmerchant.dao.UsersDAO;
import vungnv.com.foodappmerchant.model.Order;
import vungnv.com.foodappmerchant.model.UserModel;
import vungnv.com.foodappmerchant.utils.createNotification;
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
    private String idMerchant = "";
    private int temp = 0;
    private ArrayList<Order> aListOrder = new ArrayList<>();

    createNotificationChannel notification = new createNotificationChannel();
    createNotification createNotification = new createNotification();

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
        //refreshList(idMerchant);
        // refreshWaitingTime();


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
            notification.mCreateNotificationChannel(requireContext());
        }
    }

    private void getListOrder(String idMerchant) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListOrder.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    assert order != null;
                    if (order.status == 1) {
                        aListOrder.add(order);
                        setData(aListOrder);
                    }

                }
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
            orderAdapter = new OrderAdapter(getContext(), aListOrder);
            rcvListOrder.setAdapter(orderAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            rcvListOrder.setLayoutManager(linearLayoutManager);
            if (temp == 0) {
                Toast.makeText(getContext(), "Hiện không có đơn nào !", Toast.LENGTH_SHORT).show();
            }
            temp++;

            return;
        }
        for (int i = 0; i < aListOrder.size(); i++) {
            Order item = new Order();
            Order value = aListOrder.get(i);
            item.pos = value.pos;
            item.id = value.id;
            item.idUser = value.idUser;
            item.dateTime = value.dateTime;
            item.waitingTime = value.waitingTime;
            item.items = value.items;
            item.quantity = value.quantity;
            item.status = value.status;
            item.check = 1;
            item.price = value.price;
            item.notes = value.notes;
            if (orderDAO.insert(item) > 0) {
                Log.d(TAG, "save data to local db order success");
            } else {
                // Log.d(TAG, "save data to local db order fail or item exist");
                Order item1 = new Order();
                item1.id = value.id;
                item1.waitingTime = value.waitingTime;
                if (orderDAO.updateWaitingTime(item1) > 0) {
                    Log.d(TAG, "update time success with idOrder: " + value.id + " current time: " + value.waitingTime);
                } else {
                    Log.d(TAG, "update time fail");
                }
            }
        }
        refreshList();
        //showListOrder();


    }

    private void showListOrder() {

        listOrder = orderDAO.getALLDefault(1);
        for (int i = 0; i < listOrder.size(); i++) {
            if (listOrder.get(i).waitingTime == 1) {
                Order item = new Order();
                item.id = listOrder.get(i).id;
                item.check = 0;
                if (orderDAO.updateCheck(item) > 0) {
                    Log.d(TAG, "update check success 1 -> 0");
                } else {
                    Log.d(TAG, "update order out of time fail");
                }
            }
        }

        orderAdapter = new OrderAdapter(getContext(), listOrder);
        rcvListOrder.setAdapter(orderAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvListOrder.setLayoutManager(linearLayoutManager);

    }

    private void deleteListOrder(String idMerchant, int pos) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("list_order").child(idMerchant).child(String.valueOf(pos));
        reference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Data deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting data", e);
                    }
                });
    }

    private void updateStatusListOrder(String idMerchant, int pos, String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child("list_order")
                .child(idMerchant).child(String.valueOf(pos)).child("status");
        ref.setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), AUTO_CANCEL + "\nWith id: " + id, Toast.LENGTH_SHORT).show();
                Log.d(TAG, AUTO_CANCEL);
            }
        });
    }

    private void refreshList() {
        Thread t1 = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {

                while (true) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listOrder = orderDAO.getALLDefault(1);
                            for (int i = 0; i < listOrder.size(); i++) {
                                if (listOrder.get(i).waitingTime == 1) {
                                    Order item = new Order();
                                    item.id = listOrder.get(i).id;
                                    item.check = 0;
                                    if (orderDAO.updateCheck(item) > 0) {
                                        Log.d(TAG, "update check success 1 -> 0");
                                    } else {
                                        Log.d(TAG, "update order out of time fail");
                                    }
                                }
                            }
                            if (listOrder.size() == 0) {
                                orderAdapter = new OrderAdapter(getContext(), listOrder);
                                rcvListOrder.setAdapter(orderAdapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                                rcvListOrder.setLayoutManager(linearLayoutManager);
                                return;
                            }
                            orderAdapter = new OrderAdapter(getContext(), listOrder);
                            rcvListOrder.setAdapter(orderAdapter);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            rcvListOrder.setLayoutManager(linearLayoutManager);

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